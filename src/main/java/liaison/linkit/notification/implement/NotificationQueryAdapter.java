package liaison.linkit.notification.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class NotificationQueryAdapter {
    private final NotificationRepository notificationRepository;
}
