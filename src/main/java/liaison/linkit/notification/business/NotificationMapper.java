package liaison.linkit.notification.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItem;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItems;

@Mapper
public class NotificationMapper {

    public NotificationItems toNotificationItems(final List<Notification> notifications) {
        List<NotificationItem> notificationItems = notifications.stream()
                .map(this::toNotificationItem) // 각 Notification을 NotificationItem으로 변환
                .toList(); // 변환된 리스트 생성

        return NotificationItems.builder()
                .notificationItems(notificationItems) // 리스트를 NotificationItems에 설정
                .build();
    }

    public NotificationItem toNotificationItem(final Notification notification) {
        return NotificationItem.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .notificationStatus(notification.getNotificationStatus())
                .createdAt(notification.getCreatedAt())
                .modifiedAt(notification.getModifiedAt())
                .build();
    }
}
