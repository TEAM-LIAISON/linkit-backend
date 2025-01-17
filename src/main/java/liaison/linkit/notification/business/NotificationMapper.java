package liaison.linkit.notification.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationCountResponse;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
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
        NotificationDetails notificationDetails = switch (notification.getNotificationType()) {
            case TEAM_INVITATION -> NotificationDetails.invitation(
                    notification.getInvitationDetails().getTeamName()
            );
            case CHATTING -> NotificationDetails.chat(
                    notification.getChatDetails().getSenderName(),
                    notification.getChatDetails().getReceiverName(),
                    null  // lastMessage는 필요에 따라 추가
            );
            case MATCHING -> NotificationDetails.matching(
                    notification.getMatchingDetails().getMatchingSenderName(),
                    notification.getMatchingDetails().getMatchingReceiverName(),
                    notification.getNotificationStatus().name()
            );
            case SYSTEM -> NotificationDetails.system(
                    notification.getSystemDetails().getSystemMessage()
            );
        };

        return NotificationItem.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .notificationStatus(notification.getNotificationStatus())
                .createdAt(notification.getCreatedAt())
                .modifiedAt(notification.getModifiedAt())
                .build();
    }

    public NotificationCountResponse toNotificationCount(final long unreadNotificationCount, final long unreadChatCount) {
        return NotificationCountResponse.builder()
                .unreadChatCount(unreadChatCount)
                .unreadNotificationCount(unreadNotificationCount)
                .build();
    }
}
