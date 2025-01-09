package liaison.linkit.notification.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "notifications")
@TypeAlias("Notification")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Notification {

    @Id
    private String id;

    @Field("receiver_member_id")
    private Long receiverMemberId;

    @Field("notification_type")
    private NotificationType notificationType;      // 알림 타입

    @Field("notification_status")
    private NotificationStatus notificationStatus;  // 알림 상태

    private LocalDateTime createdAt;                // 생성 시간
    private LocalDateTime modifiedAt;               // 수정 시간

    private InvitationDetails invitationDetails;
    private ChatDetails chatDetails;
    private MatchingDetails matchingDetails;
    private SystemDetails systemDetails;

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

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class SystemDetails {
        private String systemMessage;
    }

    public void setMatchingDetails(final MatchingDetails matchingDetails) {
        this.matchingDetails = matchingDetails;
    }

    public void setChatDetails(final ChatDetails chatDetails) {
        this.chatDetails = chatDetails;
    }

    public void setInvitationDetails(final InvitationDetails invitationDetails) {
        this.invitationDetails = invitationDetails;
    }

    public void setSystemDetails(final SystemDetails systemDetails) {
        this.systemDetails = systemDetails;
    }
}
