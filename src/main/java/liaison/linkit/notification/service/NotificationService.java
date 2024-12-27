package liaison.linkit.notification.service;

import java.util.List;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final NotificationQueryAdapter notificationQueryAdapter;
    private final NotificationMapper notificationMapper;

    public NotificationResponseDTO.NotificationItems getNotificationItems(final String memberId) {
        final List<Notification> notifications = notificationQueryAdapter.getNotificationsByMember(memberId);
        return notificationMapper.toNotificationItems(notifications);
    }
}
