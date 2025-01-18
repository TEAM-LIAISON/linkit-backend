package liaison.linkit.notification.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

        // 채팅 알림
        private String chatSenderName;

        // 매칭 알림
        private String matchingTargetName;

        // 팀 알림 / 팀 초대 알림
        private String teamMemberName;
        private String teamName;

        @Builder
        private NotificationDetails(
                final String chatSenderName,
                final String matchingTargetName,
                final String teamMemberName,
                final String teamName
        ) {
            this.chatSenderName = chatSenderName;
            this.matchingTargetName = matchingTargetName;
            this.teamMemberName = teamMemberName;
            this.teamName = teamName;
        }

        // 팀 초대 요청에 대한 알림생성
        public static NotificationDetails teamInvitationRequested(final String teamName) {
            return NotificationDetails.builder()
                    .teamName(teamName)
                    .build();
        }

        // 팀 초대 완료에 대한 알림 생성
        public static NotificationDetails teamMemberJoined(final String teamMemberName, final String teamName) {
            return NotificationDetails.builder()
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .build();
        }

        // 채팅 알림 생성
        public static NotificationDetails chat(final String chatSenderName) {
            return NotificationDetails.builder()
                    .chatSenderName(chatSenderName)
                    .build();
        }

        // 매칭 알림 생성
        public static NotificationDetails matchingRequested(final String matchingTargetName) {
            return NotificationDetails.builder()
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 매칭 성사 알림 생성
        public static NotificationDetails matchingAccepted(final String matchingTargetName) {
            return NotificationDetails.builder()
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 매칭 거절 알림 생성
        public static NotificationDetails matchingRejected(final String matchingTargetName) {
            return NotificationDetails.builder()
                    .matchingTargetName(matchingTargetName)
                    .build();
        }

        // 팀 삭제 요청 알림 생성
        public static NotificationDetails removeTeamRequested(final String teamMemberName, final String teamName) {
            return NotificationDetails.builder()
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .build();
        }

        // 팀 삭제 요청 거절 알림 생성
        public static NotificationDetails removeTeamRejected(final String teamMemberName, final String teamName) {
            return NotificationDetails.builder()
                    .teamMemberName(teamMemberName)
                    .teamName(teamName)
                    .build();
        }

        // 팀 삭제 요청 거절 알림 생성
        public static NotificationDetails removeTeamCompleted(final String teamName) {
            return NotificationDetails.builder()
                    .teamName(teamName)
                    .build();
        }
    }
}
