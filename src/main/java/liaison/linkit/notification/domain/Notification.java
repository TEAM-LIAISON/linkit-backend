package liaison.linkit.notification.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
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

    @Id private String id;

    @Field("receiver_member_id")
    private Long receiverMemberId;

    @Field("notification_type")
    private NotificationType notificationType; // 알림 타입

    @Field("sub_notification_type")
    private SubNotificationType subNotificationType; // 알림 보조 타입

    @Field("notification_read_status")
    private NotificationReadStatus notificationReadStatus; // 알림 상태

    private LocalDateTime createdAt; // 생성 시간

    private MatchingDetails matchingDetails;
    private AnnouncementDetails announcementDetails;
    private ChatDetails chatDetails;

    private TeamInvitationDetails teamInvitationDetails;
    private TeamDetails teamDetails;

    private SystemDetails systemDetails;

    private CertificationDetails certificationDetails;

    private VisitorDetails visitorDetails;

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class MatchingDetails {

        private Long matchingId;
        private String matchingTargetImagePath;
        private String matchingTargetName;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class AnnouncementDetails {

        private Long matchingId;
        private String matchingTargetImagePath;
        private String matchingTargetName;
        private String majorPosition;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class ChatDetails {

        private String chatRoomId;
        private String chatSenderImagePath;
        private String chatSenderName;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class TeamInvitationDetails {

        private String teamCode;
        private String teamLogoImagePath;
        private String teamMemberName;
        private String teamName;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class TeamDetails {

        private String teamCode;
        private String teamLogoImagePath;
        private String teamMemberName;
        private String teamName;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class SystemDetails {

        private String emailId;
        private String systemTitle;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class CertificationDetails {

        private Long itemId;
        private String itemType;
    }

    @Data
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class VisitorDetails {

        private String visitorName;
        private String visitorIdentifier;
        private Long visitorCount;
        private String visitedType; // PROFILE, TEAM
    }

    public void setMatchingDetails(final MatchingDetails matchingDetails) {
        this.matchingDetails = matchingDetails;
    }

    public void setAnnouncementDetails(final AnnouncementDetails announcementDetails) {
        this.announcementDetails = announcementDetails;
    }

    public void setChatDetails(final ChatDetails chatDetails) {
        this.chatDetails = chatDetails;
    }

    public void setTeamInvitationDetails(final TeamInvitationDetails teamInvitationDetails) {
        this.teamInvitationDetails = teamInvitationDetails;
    }

    public void setTeamDetails(final TeamDetails teamDetails) {
        this.teamDetails = teamDetails;
    }

    public void setSystemDetails(final SystemDetails systemDetails) {
        this.systemDetails = systemDetails;
    }

    public void setCertificationDetails(final CertificationDetails certificationDetails) {
        this.certificationDetails = certificationDetails;
    }

    public void setVisitorDetails(final VisitorDetails visitorDetails) {
        this.visitorDetails = visitorDetails;
    }

    public void setNotificationReadStatus(final NotificationReadStatus notificationReadStatus) {
        this.notificationReadStatus = notificationReadStatus;
    }
}
