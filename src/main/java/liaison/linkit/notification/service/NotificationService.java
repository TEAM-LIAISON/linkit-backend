package liaison.linkit.notification.service;

import java.util.List;
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

    public NotificationResponseDTO.NotificationItems getNotificationItems(final Long memberId) {
        final List<Notification> notifications = notificationQueryAdapter.getNotificationsByMember(memberId);
        return notificationMapper.toNotificationItems(notifications);
    }

    public void alertNewNotification(final Notification notification) {
        // DB에 알림 저장
        Notification savedNotification = notificationCommandAdapter.save(notification);

        final String emailId = memberQueryAdapter.findEmailIdById(notification.getReceiverMemberId());

        // WebSocket을 통해 실시간 알림 전송
        NotificationResponseDTO.NotificationItem notificationItem = notificationMapper.toNotificationItem(savedNotification);

        if (emailId != null) {
            messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, notificationItem);
            log.info("Sent notification to " + emailId);
        } else {
            log.warn("수신자의 emailId가 존재하지 않습니다. 알림 전송이 중단되었습니다.");
        }
    }

    public ReadNotificationResponse readNotification(final Long memberId, final String notificationId) {
        // 1) 알림 조회
        Notification notification = notificationQueryAdapter.findById(notificationId);

        notification.setNotificationReadStatus(NotificationReadStatus.READ);
        final Notification savedNotification = notificationCommandAdapter.save(notification);

        log.info("memberId={}가 알림(notificationId={})을 읽음 처리했습니다.", memberId, notificationId);
        return notificationMapper.toReadNotification(savedNotification);
    }
}
