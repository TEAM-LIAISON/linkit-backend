package liaison.linkit.notification.business;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationCountResponse;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItem;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItems;

@Mapper
public class NotificationMapper {

    public Notification toNotification(
            final Long notificationReceiverMemberId,
            final NotificationType notificationType,
            final SubNotificationType subNotificationType,
            final NotificationDetails notificationDetails
    ) {
        Notification notification = Notification.builder()
                .receiverMemberId(notificationReceiverMemberId)
                .notificationType(notificationType)
                .subNotificationType(subNotificationType)
                .notificationReadStatus(NotificationReadStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        // 알림 타입에 따라 세부 정보 설정
        switch (notificationType) {
            // 매칭 케이스
            case MATCHING -> {
                switch (subNotificationType) {
                    case MATCHING_REQUESTED, MATCHING_REJECTED, MATCHING_ACCEPTED -> notification.setMatchingDetails(
                            Notification.MatchingDetails.builder()
                                    .matchingId(notificationDetails.getMatchingId())
                                    .matchingTargetImagePath(notificationDetails.getMatchingTargetLogoImagePath())
                                    .matchingTargetName(notificationDetails.getMatchingTargetName())
                                    .build()
                    );
                }
            }

            // 채팅 케이스
            case CHATTING -> {
                switch (subNotificationType) {
                    case NEW_CHAT -> notification.setChatDetails(
                            Notification.ChatDetails.builder()
                                    .chatRoomId(notificationDetails.getChatRoomId())
                                    .chatSenderImagePath(notificationDetails.getChatSenderLogoImagePath())
                                    .chatSenderName(notificationDetails.getChatSenderName())
                                    .build()
                    );
                }
            }

            case TEAM_INVITATION -> {
                switch (subNotificationType) {
                    case TEAM_INVITATION_REQUESTED -> notification.setTeamInvitationDetails(
                            Notification.TeamInvitationDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamName(notificationDetails.getTeamName())
                                    .build()
                    );

                    case TEAM_MEMBER_JOINED -> notification.setTeamInvitationDetails(
                            Notification.TeamInvitationDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamMemberName(notificationDetails.getTeamMemberName())
                                    .teamName(notificationDetails.getTeamName())
                                    .build()
                    );
                }
            }

            case TEAM -> {
                switch (subNotificationType) {
                    case REMOVE_TEAM_REQUESTED, REMOVE_TEAM_REJECTED -> notification.setTeamDetails(
                            Notification.TeamDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamMemberName(notificationDetails.getTeamMemberName())
                                    .teamName(notificationDetails.getTeamName())
                                    .build()
                    );

                    case TEAM_MEMBER_JOINED -> notification.setTeamDetails(
                            Notification.TeamDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamName(notificationDetails.getTeamName())
                                    .build()
                    );
                }
            }
        }

        return notification;
    }

    public NotificationItems toNotificationItems(final List<Notification> notifications) {
        List<NotificationItem> notificationItems = notifications.stream()
                .map(this::toNotificationItem) // 각 Notification을 NotificationItem으로 변환
                .toList(); // 변환된 리스트 생성

        return NotificationItems.builder()
                .notificationItems(notificationItems) // 리스트를 NotificationItems에 설정
                .build();
    }

    public NotificationItem toNotificationItem(final Notification notification) {
        // 상위 타입과 하위 서브 타입
        final NotificationType type = notification.getNotificationType();
        final SubNotificationType subType = notification.getSubNotificationType();

        NotificationDetails notificationDetails = null;

        switch (type) {
            // 1) MATCHING 타입
            case MATCHING -> {
                switch (subType) {
                    case MATCHING_REQUESTED -> notificationDetails = NotificationDetails.matchingRequested(
                            notification.getMatchingDetails().getMatchingId(),
                            notification.getMatchingDetails().getMatchingTargetImagePath(),
                            notification.getMatchingDetails().getMatchingTargetName()
                    );

                    case MATCHING_REJECTED -> notificationDetails = NotificationDetails.matchingRejected(
                            notification.getMatchingDetails().getMatchingId(),
                            notification.getMatchingDetails().getMatchingTargetImagePath(),
                            notification.getMatchingDetails().getMatchingTargetName()
                    );

                    case MATCHING_ACCEPTED -> notificationDetails = NotificationDetails.matchingAccepted(
                            notification.getMatchingDetails().getMatchingId(),
                            notification.getMatchingDetails().getMatchingTargetImagePath(),
                            notification.getMatchingDetails().getMatchingTargetName()
                    );
                }
            }

            // 2) CHATTING 타입
            case CHATTING -> {
                switch (subType) {
                    case NEW_CHAT -> notificationDetails = NotificationDetails.chat(
                            notification.getChatDetails().getChatRoomId(),
                            notification.getChatDetails().getChatSenderImagePath(),
                            notification.getChatDetails().getChatSenderName()
                    );
                }
            }

            // 3) TEAM_INVITATION 타입
            case TEAM_INVITATION -> {
                switch (subType) {
                    case TEAM_INVITATION_REQUESTED -> notificationDetails = NotificationDetails.teamInvitationRequested(
                            notification.getTeamInvitationDetails().getTeamCode(),
                            notification.getTeamInvitationDetails().getTeamLogoImagePath(),
                            notification.getTeamInvitationDetails().getTeamName()
                    );
                    case TEAM_MEMBER_JOINED -> notificationDetails = NotificationDetails.teamMemberJoined(
                            notification.getTeamInvitationDetails().getTeamCode(),
                            notification.getTeamInvitationDetails().getTeamLogoImagePath(),
                            notification.getTeamInvitationDetails().getTeamMemberName(),
                            notification.getTeamInvitationDetails().getTeamName()
                    );
                }
            }

            // 4) TEAM 타입
            case TEAM -> {
                switch (subType) {
                    case REMOVE_TEAM_REQUESTED -> notificationDetails = NotificationDetails.removeTeamRequested(
                            notification.getTeamDetails().getTeamCode(),
                            notification.getTeamDetails().getTeamLogoImagePath(),
                            notification.getTeamDetails().getTeamMemberName(),
                            notification.getTeamDetails().getTeamName()
                    );

                    case REMOVE_TEAM_REJECTED -> notificationDetails = NotificationDetails.removeTeamRejected(
                            notification.getTeamDetails().getTeamCode(),
                            notification.getTeamDetails().getTeamLogoImagePath(),
                            notification.getTeamDetails().getTeamMemberName(),
                            notification.getTeamDetails().getTeamName()
                    );

                    case REMOVE_TEAM_COMPLETED -> notificationDetails = NotificationDetails.removeTeamCompleted(
                            notification.getTeamDetails().getTeamCode(),
                            notification.getTeamDetails().getTeamLogoImagePath(),
                            notification.getTeamDetails().getTeamName()
                    );
                }
            }
        }

        return NotificationItem.builder()
                .notificationType(type)
                .subNotificationType(subType)
                .notificationReadStatus(notification.getNotificationReadStatus())
                .notificationOccurTime(DateUtils.formatRelativeTime(notification.getCreatedAt()))
                .notificationDetails(notificationDetails)
                .build();
    }

    public NotificationCountResponse toNotificationCount(final long unreadChatCount, final long unreadNotificationCount) {
        return NotificationCountResponse.builder()
                .unreadChatCount(unreadChatCount)
                .unreadNotificationCount(unreadNotificationCount)
                .build();
    }
}
