package liaison.linkit.notification.service;

import java.util.List;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
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
            // 수신자별 구독 주소로 알림 전송
            log.info("Sent notification to " + emailId);
        } else {
            log.warn("수신자의 emailId가 존재하지 않습니다. 알림 전송이 중단되었습니다.");
        }
    }
}
