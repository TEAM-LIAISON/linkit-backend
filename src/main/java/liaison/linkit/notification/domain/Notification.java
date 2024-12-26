package liaison.linkit.notification.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Notification {
    @Id
    private String id;

    private NotificationType notificationType;      // 알림 타입
    private NotificationStatus notificationStatus;  // 알림 상태

    private LocalDateTime createdAt;                // 생성 시간
    private LocalDateTime modifiedAt;               // 수정 시간

    private InvitationDetails invitationDetails;
    private ChatDetails chatDetails;
    private MatchingDetails matchingDetails;

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class InvitationDetails {
        private String teamName;                    // 발신 팀 이름
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class ChatDetails {
        private String senderName;                  // 채팅 발신자 이름
        private String receiverName;                // 채팅 수신자 이름
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class MatchingDetails {
        private String matchingSenderName;          // 매칭 요청 발신자 이름
        private String matchingReceiverName;        // 매칭 요청 수신자 이름
    }
}
