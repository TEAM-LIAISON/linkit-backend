package liaison.linkit.notification.service;

import java.util.List;

import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.implement.ChatMessageQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.ReadNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationQueryAdapter notificationQueryAdapter;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final NotificationMapper notificationMapper;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final ChatMessageQueryAdapter chatMessageQueryAdapter;

    public NotificationResponseDTO.NotificationItems getNotificationItems(final Long memberId) {
        final List<Notification> notifications =
                notificationQueryAdapter.getNotificationsByMember(memberId);
        return notificationMapper.toNotificationItems(notifications);
    }

    public NotificationResponseDTO.NotificationCountResponse getNotificationCount(
            final Long memberId) {
        return getUnreadCount(memberId);
    }

    public void alertNewNotification(final Notification notification) {
        // DB에 알림 저장
        Notification savedNotification = notificationCommandAdapter.save(notification);

        final String emailId =
                memberQueryAdapter.findEmailIdById(notification.getReceiverMemberId());

        // WebSocket을 통해 실시간 알림 전송
        NotificationResponseDTO.NotificationItem notificationItem =
                notificationMapper.toNotificationItem(savedNotification);

        if (emailId != null) {
            messagingTemplate.convertAndSend(
                    "/sub/notification/header/" + emailId, notificationItem);
        } else {
            log.warn("수신자의 emailId가 존재하지 않습니다. 알림 전송이 중단되었습니다.");
        }
    }

    public ReadNotificationResponse readNotification(
            final Long memberId, final String notificationId) {
        // 1) 알림 조회
        Notification notification = notificationQueryAdapter.findById(notificationId);

        notification.setNotificationReadStatus(NotificationReadStatus.READ);
        final Notification savedNotification = notificationCommandAdapter.save(notification);

        //        headerNotificationService.publishNotificationCount(memberId);

        return notificationMapper.toReadNotification(savedNotification);
    }

    private NotificationResponseDTO.NotificationCountResponse getUnreadCount(final Long memberId) {
        long unreadNotificationCount = notificationQueryAdapter.countUnreadMessages(memberId);
        // 4. 채팅방 참여 이력이 없을 경우 처리
        long unreadChatCount = 0;
        if (chatRoomQueryAdapter.existsChatRoomByMemberId(memberId)) {
            // 2. 사용자의 채팅방 참여 이력 조회
            List<ChatRoom> chatRooms = chatRoomQueryAdapter.findAllChatRoomsByMemberId(memberId);

            // 3. 채팅방 ID 추출
            List<Long> chatRoomIds = chatRooms.stream().map(ChatRoom::getId).toList();
            unreadChatCount =
                    chatMessageQueryAdapter.countUnreadMessagesByChatRoomIdsAndReceiver(
                            memberId, chatRoomIds);
        }

        return notificationMapper.toNotificationCount(unreadChatCount, unreadNotificationCount);
    }
}
