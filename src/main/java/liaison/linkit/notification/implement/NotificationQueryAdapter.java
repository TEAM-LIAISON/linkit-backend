package liaison.linkit.notification.implement;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import liaison.linkit.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class NotificationQueryAdapter {
    private final NotificationRepository notificationRepository;

    public Notification getNotificationByMember(final Long memberId) {
        return notificationRepository.findTopByReceiverMemberIdOrderByCreatedAtDesc(memberId);
    }

    public List<Notification> getNotificationsByMember(final Long memberId) {
        return notificationRepository.findAllByReceiverMemberIdOrderByCreatedAtDesc(memberId);
    }

    public long countUnreadMessages(final Long memberId) {
        return notificationRepository.countUnreadMessages(memberId);
    }

    public Notification findById(final String notificationId) {
        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.EXCEPTION);
    }
}
