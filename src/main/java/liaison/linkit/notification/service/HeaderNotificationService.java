package liaison.linkit.notification.service;

import java.util.List;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.implement.ChatQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.global.presentation.dto.SubscribeEvent;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeaderNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationQueryAdapter notificationQueryAdapter;
    private final ChatQueryAdapter chatQueryAdapter;

    private final NotificationMapper notificationMapper;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;

    public void publishNotificationCount(final Long memberId) {
        final String emailId = memberQueryAdapter.findEmailIdById(memberId);

        NotificationCountResponse count = getUnreadCount(memberId);
        log.info("Sent notification to " + emailId);
        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    }

    @EventListener
    public void handleSubscribeEvent(final SubscribeEvent event) {
        Long memberId = event.getMemberId();
        String emailId = event.getEmailId();
        log.info("Subscribed to " + emailId);
        log.info("Subscribed to " + memberId);

        // 초기 데이터 전송
        NotificationCountResponse count = getUnreadCount(memberId);
        log.info("Sent notification to " + emailId);
        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    }

    public NotificationCountResponse getUnreadCount(final Long memberId) {
        long unreadNotificationCount = notificationQueryAdapter.countUnreadMessages(memberId);
        log.info("Unread notification count: " + unreadNotificationCount);
        // 4. 채팅방 참여 이력이 없을 경우 처리
        long unreadChatCount = 0;
        if (chatRoomQueryAdapter.existsChatRoomByMemberId(memberId)) {
            log.info("Chat room exists");
            // 2. 사용자의 채팅방 참여 이력 조회
            List<ChatRoom> chatRooms = chatRoomQueryAdapter.findAllChatRoomsByMemberId(memberId);
            log.info("Found " + chatRooms.size() + " chat rooms");

            // 3. 채팅방 ID 추출
            List<Long> chatRoomIds = chatRooms.stream()
                    .map(ChatRoom::getId)
                    .toList();
            log.info("Found " + chatRoomIds.size() + " chat rooms");
            unreadChatCount = chatQueryAdapter.countUnreadMessagesByChatRoomIdsAndReceiver(memberId, chatRoomIds);
        }

        return notificationMapper.toNotificationCount(unreadChatCount, unreadNotificationCount);
    }
}
