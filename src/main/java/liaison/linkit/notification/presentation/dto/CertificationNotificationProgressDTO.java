package liaison.linkit.notification.presentation.dto;

import java.time.LocalDateTime;

import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationNotificationProgressDTO {

    // 처리할 알림 객체
    private String notificationId;
    private Long receiverMemberId;
    private NotificationType notificationType;
    private SubNotificationType subNotificationType;
    private LocalDateTime createdAt;

    // 인증 항목 관련 필드
    private Long itemId; // 인증된 항목의 ID
    private String itemType; // 항목 타입 (ACTIVITY, AWARDS, EDUCATION, LICENSE)
    private String itemName; // 항목명
}
