package liaison.linkit.notification.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
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
    public static class ReadNotificationResponse {
        private String notificationId;
        private NotificationReadStatus notificationReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomConnectedInitResponse {
        // 상대방의 온라인 여부
        private boolean isChatPartnerOnline;

        // 읽지 않은 채팅 메시지 개수
        private long unreadChatMessageCount;

        // 마지막 메시지
        private String lastMessage;

        // 마지막 메시지의 시간
        private LocalDateTime lastMessageTime;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCountResponse {
        private long unreadChatCount;
        private long unreadNotificationCount;
    }

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
        private String notificationId;

        private NotificationType notificationType;
        private SubNotificationType subNotificationType;
        private NotificationReadStatus notificationReadStatus;
        private String notificationOccurTime;

        // 알림 타입별 상세 정보
        private NotificationDetails notificationDetails;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NotificationDetails {

        // 이메일 ID
        private String emailId;

        // 채팅 알림
        private String chatRoomId;
        private String chatSenderLogoImagePath;
        private String chatSenderName;

        // 매칭 알림
        private Long matchingId;
        private String matchingTargetLogoImagePath;
        private String matchingTargetName;

        // 팀 알림 / 팀 초대 알림
        private String teamCode;
        private String teamLogoImagePath;
        private String teamMemberName;
        private String teamName;

        private Boolean isTeamDeleted;

        // 시스템 알림
        private String systemTitle;

        @Builder
        private NotificationDetails(
                final String emailId,

                final String chatRoomId,
                final String chatSenderLogoImagePath,
                final String chatSenderName,

                final Long matchingId,
                final String matchingTargetLogoImagePath,
                final String matchingTargetName,

                final String teamCode,
                final String teamLogoImagePath,
                final String teamMemberName,
                final String teamName,

                final Boolean isTeamDeleted,

                final String systemTitle
        ) {
            this.emailId = emailId;

            this.chatRoomId = chatRoomId;
            this.chatSenderLogoImagePath = chatSenderLogoImagePath;
            this.chatSenderName = chatSenderName;

            this.matchingId = matchingId;
            this.matchingTargetLogoImagePath = matchingTargetLogoImagePath;
            this.matchingTargetName = matchingTargetName;

            this.teamCode = teamCode;
            this.teamLogoImagePath = teamLogoImagePath;
            this.teamMemberName = teamMemberName;
            this.teamName = teamName;

            this.isTeamDeleted = isTeamDeleted;

            this.systemTitle = systemTitle;
        }

        // 팀 초대 요청 알림 생성 (팀 삭제 여부 포함)
        public static NotificationDetails teamInvitationRequested(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamName,
                final boolean isTeamDeleted
        ) {
            return NotificationDetails.builder()
                    .teamCode(teamCode)
                    .teamLogoImagePath(teamLogoImagePath)
                    .teamName(teamName)
                    .isTeamDeleted(isTeamDeleted)
                    .build();
        }

        // 팀 멤버 가입 알림 생성 (팀 삭제 여부 포함)
        public static NotificationDetails teamMemberJoined(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamMemberName,
                final String teamName,
                final boolean isTeamDeleted
        ) {
            return NotificationDetails.builder()
                    .teamCode(teamCode)
                    .teamLogoImagePath(teamLogoImagePath)
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .isTeamDeleted(isTeamDeleted)
                    .build();
        }

        // 채팅 알림 생성
        public static NotificationDetails chat(
                final String chatRoomId,
                final String chatSenderLogoImagePath,
                final String chatSenderName
        ) {
            return NotificationDetails.builder()
                    .chatRoomId(chatRoomId)
                    .chatSenderLogoImagePath(chatSenderLogoImagePath)
                    .chatSenderName(chatSenderName)
                    .build();
        }

        // 매칭 알림 생성
        public static NotificationDetails matchingRequested(
                final Long matchingId,
                final String matchingTargetLogoImagePath,
                final String matchingTargetName
        ) {
            return NotificationDetails.builder()
                    .matchingId(matchingId)
                    .matchingTargetLogoImagePath(matchingTargetLogoImagePath)
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 매칭 성사 알림 생성
        public static NotificationDetails matchingAccepted(
                final Long matchingId,
                final String matchingTargetLogoImagePath,
                final String matchingTargetName
        ) {
            return NotificationDetails.builder()
                    .matchingId(matchingId)
                    .matchingTargetLogoImagePath(matchingTargetLogoImagePath)
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 매칭 거절 알림 생성
        public static NotificationDetails matchingRejected(
                final Long matchingId,
                final String matchingTargetLogoImagePath,
                final String matchingTargetName
        ) {
            return NotificationDetails.builder()
                    .matchingId(matchingId)
                    .matchingTargetLogoImagePath(matchingTargetLogoImagePath)
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 팀 관련 기타 알림 생성 메서드도 동일하게 isTeamDeleted 인자를 추가하여 구현
        public static NotificationDetails removeTeamRequested(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamMemberName,
                final String teamName,
                final boolean isTeamDeleted
        ) {
            return NotificationDetails.builder()
                    .teamCode(teamCode)
                    .teamLogoImagePath(teamLogoImagePath)
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .isTeamDeleted(isTeamDeleted)
                    .build();
        }

        // 팀 삭제 요청 거절 알림 생성
        public static NotificationDetails removeTeamRejected(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamMemberName,
                final String teamName,
                final boolean isTeamDeleted
        ) {
            return NotificationDetails.builder()
                    .teamCode(teamCode)
                    .teamLogoImagePath(teamLogoImagePath)
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .isTeamDeleted(isTeamDeleted)
                    .build();
        }

        // 팀 삭제 요청 거절 알림 생성
        public static NotificationDetails removeTeamCompleted(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamName,
                final boolean isTeamDeleted
        ) {
            return NotificationDetails.builder()
                    .teamCode(teamCode)
                    .teamLogoImagePath(teamLogoImagePath)
                    .teamName(teamName)
                    .isTeamDeleted(isTeamDeleted)
                    .build();
        }

        // 링킷 환영 알림
        public static NotificationDetails welcomeLinkit(
                final String emailId,
                final String systemTitle
        ) {
            return NotificationDetails.builder()
                    .emailId(emailId)
                    .systemTitle(systemTitle)
                    .build();
        }
    }
}
