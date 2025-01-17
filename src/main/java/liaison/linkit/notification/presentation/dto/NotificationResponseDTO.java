package liaison.linkit.notification.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        private String id;
        private NotificationType notificationType;
        private NotificationStatus notificationStatus;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        // 알림 타입별 상세 정보
        private NotificationDetails notificationDetails;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NotificationDetails {
        // 초대 알림
        private String teamName;

        // 채팅 알림
        private String senderName;
        private String receiverName;
        private String lastMessage;

        // 매칭 알림
        private String matchingSenderName;
        private String matchingReceiverName;
        private String matchingStatus;

        // 시스템 알림
        private String systemMessage;

        @Builder
        private NotificationDetails(
                String teamName,
                String senderName, String receiverName, String lastMessage,
                String matchingSenderName, String matchingReceiverName, String matchingStatus,
                String systemMessage
        ) {
            this.teamName = teamName;
            this.senderName = senderName;
            this.receiverName = receiverName;
            this.lastMessage = lastMessage;
            this.matchingSenderName = matchingSenderName;
            this.matchingReceiverName = matchingReceiverName;
            this.matchingStatus = matchingStatus;
            this.systemMessage = systemMessage;
        }

        // 초대 알림 생성
        public static NotificationDetails invitation(String teamName) {
            return NotificationDetails.builder()
                    .teamName(teamName)
                    .build();
        }

        // 채팅 알림 생성
        public static NotificationDetails chat(String senderName, String receiverName, String lastMessage) {
            return NotificationDetails.builder()
                    .senderName(senderName)
                    .receiverName(receiverName)
                    .lastMessage(lastMessage)
                    .build();
        }

        // 매칭 알림 생성
        public static NotificationDetails matching(String senderName, String receiverName, String status) {
            return NotificationDetails.builder()
                    .matchingSenderName(senderName)
                    .matchingReceiverName(receiverName)
                    .matchingStatus(status)
                    .build();
        }

        // 매칭 알림 생성
        public static NotificationDetails system(String systemMessage) {
            return NotificationDetails.builder()
                    .systemMessage(systemMessage)
                    .build();
        }
    }
}
