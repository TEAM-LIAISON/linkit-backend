package liaison.linkit.notification.presentation.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationItems {
        @Builder.Default
        private List<NotificationItem> notificationItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationItem {
        private String id;
        private NotificationType notificationType;
        private NotificationStatus notificationStatus;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
