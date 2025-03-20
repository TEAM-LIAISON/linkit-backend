package liaison.linkit.notification.business;

import java.time.LocalDateTime;
import java.util.List;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationCountResponse;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItem;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItems;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.ReadNotificationResponse;
import liaison.linkit.team.implement.team.TeamQueryAdapter;

@Mapper
public class NotificationMapper {

    private final TeamQueryAdapter teamQueryAdapter;

    public NotificationMapper(TeamQueryAdapter teamQueryAdapter) {
        this.teamQueryAdapter = teamQueryAdapter;
    }

    public NotificationResponseDTO.ReadNotificationResponse toReadNotification(
            final Notification notification) {
        return ReadNotificationResponse.builder()
                .notificationId(notification.getId())
                .notificationReadStatus(notification.getNotificationReadStatus())
                .build();
    }

    public Notification toNotification(
            final Long notificationReceiverMemberId,
            final NotificationType notificationType,
            final SubNotificationType subNotificationType,
            final NotificationDetails notificationDetails) {
        Notification notification =
                Notification.builder()
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
                    case MATCHING_REQUESTED, MATCHING_REJECTED, MATCHING_ACCEPTED -> notification
                            .setMatchingDetails(
                                    Notification.MatchingDetails.builder()
                                            .matchingId(notificationDetails.getMatchingId())
                                            .matchingTargetImagePath(
                                                    notificationDetails
                                                            .getMatchingTargetLogoImagePath())
                                            .matchingTargetName(
                                                    notificationDetails.getMatchingTargetName())
                                            .build());
                }
            }

                // 공고 케이스
            case ANNOUNCEMENT -> {
                switch (subNotificationType) {
                    case ANNOUNCEMENT_REQUESTED,
                            ANNOUNCEMENT_ACCEPTED,
                            ANNOUNCEMENT_REJECTED -> notification.setAnnouncementDetails(
                            Notification.AnnouncementDetails.builder()
                                    .matchingId(notificationDetails.getMatchingId())
                                    .matchingTargetImagePath(
                                            notificationDetails.getMatchingTargetLogoImagePath())
                                    .matchingTargetName(notificationDetails.getMatchingTargetName())
                                    .majorPosition(notificationDetails.getMajorPosition())
                                    .build());
                }
            }

                // 채팅 케이스
            case CHATTING -> {
                switch (subNotificationType) {
                    case NEW_CHAT -> notification.setChatDetails(
                            Notification.ChatDetails.builder()
                                    .chatRoomId(notificationDetails.getChatRoomId())
                                    .chatSenderImagePath(
                                            notificationDetails.getChatSenderLogoImagePath())
                                    .chatSenderName(notificationDetails.getChatSenderName())
                                    .build());
                }
            }

                // 팀 초대 케이스
            case TEAM_INVITATION -> {
                switch (subNotificationType) {
                    case TEAM_INVITATION_REQUESTED -> notification.setTeamInvitationDetails(
                            Notification.TeamInvitationDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamName(notificationDetails.getTeamName())
                                    .build());

                    case TEAM_MEMBER_JOINED -> notification.setTeamInvitationDetails(
                            Notification.TeamInvitationDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamMemberName(notificationDetails.getTeamMemberName())
                                    .teamName(notificationDetails.getTeamName())
                                    .build());
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
                                    .build());

                    case REMOVE_TEAM_COMPLETED -> notification.setTeamDetails(
                            Notification.TeamDetails.builder()
                                    .teamCode(notificationDetails.getTeamCode())
                                    .teamLogoImagePath(notificationDetails.getTeamLogoImagePath())
                                    .teamName(notificationDetails.getTeamName())
                                    .build());
                }
            }

            case SYSTEM -> {
                switch (subNotificationType) {
                    case WELCOME_LINKIT -> notification.setSystemDetails(
                            Notification.SystemDetails.builder()
                                    .emailId(notificationDetails.getEmailId())
                                    .systemTitle(notificationDetails.getSystemTitle())
                                    .build());
                }
            }

                // 인증서 케이스
            case CERTIFICATION -> {
                switch (subNotificationType) {
                    case ACTIVITY_CERTIFICATION_ACCEPTED,
                            ACTIVITY_CERTIFICATION_REJECTED,
                            EDUCATION_CERTIFICATION_ACCEPTED,
                            EDUCATION_CERTIFICATION_REJECTED,
                            AWARDS_CERTIFICATION_ACCEPTED,
                            AWARDS_CERTIFICATION_REJECTED,
                            LICENSE_CERTIFICATION_ACCEPTED,
                            LICENSE_CERTIFICATION_REJECTED -> notification.setCertificationDetails(
                            Notification.CertificationDetails.builder()
                                    .itemId(notificationDetails.getItemId())
                                    .itemType(notificationDetails.getItemType())
                                    .build());
                }
            }

            case VISITOR -> {
                switch (subNotificationType) {
                    case PROFILE_VISITOR -> notification.setVisitorDetails(
                            Notification.VisitorDetails.builder()
                                    .visitorIdentifier(notificationDetails.getEmailId())
                                    .visitorCount(notificationDetails.getVisitorCount())
                                    .visitedType(notificationDetails.getVisitedType())
                                    .build());

                    case TEAM_VISITOR -> notification.setVisitorDetails(
                            Notification.VisitorDetails.builder()
                                    .visitorName(notificationDetails.getTeamName())
                                    .visitorIdentifier(notificationDetails.getTeamCode())
                                    .visitorCount(notificationDetails.getVisitorCount())
                                    .visitedType(notificationDetails.getVisitedType())
                                    .build());
                }
            }
        }

        return notification;
    }

    public NotificationItems toNotificationItems(final List<Notification> notifications) {
        List<NotificationItem> notificationItems =
                notifications.stream()
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
                    case MATCHING_REQUESTED -> notificationDetails =
                            NotificationDetails.matchingRequested(
                                    notification.getMatchingDetails().getMatchingId(),
                                    notification.getMatchingDetails().getMatchingTargetImagePath(),
                                    notification.getMatchingDetails().getMatchingTargetName());
                    case MATCHING_REJECTED -> notificationDetails =
                            NotificationDetails.matchingRejected(
                                    notification.getMatchingDetails().getMatchingId(),
                                    notification.getMatchingDetails().getMatchingTargetImagePath(),
                                    notification.getMatchingDetails().getMatchingTargetName());
                    case MATCHING_ACCEPTED -> notificationDetails =
                            NotificationDetails.matchingAccepted(
                                    notification.getMatchingDetails().getMatchingId(),
                                    notification.getMatchingDetails().getMatchingTargetImagePath(),
                                    notification.getMatchingDetails().getMatchingTargetName());
                }
            }

                // 2) ANNOUNCEMENT 타입
            case ANNOUNCEMENT -> {
                switch (subType) {
                    case ANNOUNCEMENT_REQUESTED -> notificationDetails =
                            NotificationDetails.announcementRequested(
                                    notification.getAnnouncementDetails().getMatchingId(),
                                    notification
                                            .getAnnouncementDetails()
                                            .getMatchingTargetImagePath(),
                                    notification.getAnnouncementDetails().getMatchingTargetName(),
                                    notification.getAnnouncementDetails().getMajorPosition());
                    case ANNOUNCEMENT_ACCEPTED -> notificationDetails =
                            NotificationDetails.announcementAccepted(
                                    notification.getAnnouncementDetails().getMatchingId(),
                                    notification
                                            .getAnnouncementDetails()
                                            .getMatchingTargetImagePath(),
                                    notification.getAnnouncementDetails().getMatchingTargetName(),
                                    notification.getAnnouncementDetails().getMajorPosition());
                    case ANNOUNCEMENT_REJECTED -> notificationDetails =
                            NotificationDetails.announcementRejected(
                                    notification.getAnnouncementDetails().getMatchingId(),
                                    notification
                                            .getAnnouncementDetails()
                                            .getMatchingTargetImagePath(),
                                    notification.getAnnouncementDetails().getMatchingTargetName(),
                                    notification.getAnnouncementDetails().getMajorPosition());
                }
            }

                // 3) CHATTING 타입
            case CHATTING -> {
                if (subType == SubNotificationType.NEW_CHAT) {
                    notificationDetails =
                            NotificationDetails.chat(
                                    notification.getChatDetails().getChatRoomId(),
                                    notification.getChatDetails().getChatSenderImagePath(),
                                    notification.getChatDetails().getChatSenderName());
                }
            }

                // 4) TEAM_INVITATION 타입
            case TEAM_INVITATION -> {
                switch (subType) {
                    case TEAM_INVITATION_REQUESTED -> {
                        String teamCode = notification.getTeamInvitationDetails().getTeamCode();
                        boolean isTeamDeleted = teamQueryAdapter.isTeamDeleted(teamCode);
                        notificationDetails =
                                NotificationDetails.teamInvitationRequested(
                                        teamCode,
                                        notification
                                                .getTeamInvitationDetails()
                                                .getTeamLogoImagePath(),
                                        notification.getTeamInvitationDetails().getTeamName(),
                                        isTeamDeleted);
                    }
                    case TEAM_MEMBER_JOINED -> {
                        String teamCode = notification.getTeamInvitationDetails().getTeamCode();
                        boolean isTeamDeleted = teamQueryAdapter.isTeamDeleted(teamCode);
                        notificationDetails =
                                NotificationDetails.teamMemberJoined(
                                        teamCode,
                                        notification
                                                .getTeamInvitationDetails()
                                                .getTeamLogoImagePath(),
                                        notification.getTeamInvitationDetails().getTeamMemberName(),
                                        notification.getTeamInvitationDetails().getTeamName(),
                                        isTeamDeleted);
                    }
                }
            }

                // 5) TEAM 타입
            case TEAM -> {
                switch (subType) {
                    case REMOVE_TEAM_REQUESTED -> {
                        String teamCode = notification.getTeamDetails().getTeamCode();
                        boolean isTeamDeleted = teamQueryAdapter.isTeamDeleted(teamCode);
                        notificationDetails =
                                NotificationDetails.removeTeamRequested(
                                        teamCode,
                                        notification.getTeamDetails().getTeamLogoImagePath(),
                                        notification.getTeamDetails().getTeamMemberName(),
                                        notification.getTeamDetails().getTeamName(),
                                        isTeamDeleted);
                    }
                    case REMOVE_TEAM_REJECTED -> {
                        String teamCode = notification.getTeamDetails().getTeamCode();
                        boolean isTeamDeleted = teamQueryAdapter.isTeamDeleted(teamCode);
                        notificationDetails =
                                NotificationDetails.removeTeamRejected(
                                        teamCode,
                                        notification.getTeamDetails().getTeamLogoImagePath(),
                                        notification.getTeamDetails().getTeamMemberName(),
                                        notification.getTeamDetails().getTeamName(),
                                        isTeamDeleted);
                    }
                    case REMOVE_TEAM_COMPLETED -> {
                        String teamCode = notification.getTeamDetails().getTeamCode();
                        boolean isTeamDeleted = teamQueryAdapter.isTeamDeleted(teamCode);
                        notificationDetails =
                                NotificationDetails.removeTeamCompleted(
                                        teamCode,
                                        notification.getTeamDetails().getTeamLogoImagePath(),
                                        notification.getTeamDetails().getTeamName(),
                                        isTeamDeleted);
                    }
                }
            }

                // 6) SYSTEM 타입
            case SYSTEM -> {
                switch (subType) {
                    case WELCOME_LINKIT -> notificationDetails =
                            NotificationDetails.welcomeLinkit(
                                    notification.getSystemDetails().getEmailId(),
                                    notification.getSystemDetails().getSystemTitle());
                }
            }

            case CERTIFICATION -> {
                switch (subType) {
                    case ACTIVITY_CERTIFICATION_ACCEPTED,
                            EDUCATION_CERTIFICATION_ACCEPTED,
                            AWARDS_CERTIFICATION_ACCEPTED,
                            LICENSE_CERTIFICATION_ACCEPTED -> notificationDetails =
                            NotificationDetails.certificationAccepted(
                                    notification.getCertificationDetails().getItemId(),
                                    notification.getCertificationDetails().getItemType());
                }
            }

            case VISITOR -> {
                switch (subType) {
                    case PROFILE_VISITOR -> notificationDetails =
                            NotificationDetails.profileVisitorCount(
                                    notification.getVisitorDetails().getVisitorIdentifier(),
                                    notification.getVisitorDetails().getVisitorCount(),
                                    notification.getVisitorDetails().getVisitedType());

                    case TEAM_VISITOR -> notificationDetails =
                            NotificationDetails.teamVisitorCount(
                                    notification.getVisitorDetails().getVisitorName(),
                                    notification.getVisitorDetails().getVisitorIdentifier(),
                                    notification.getVisitorDetails().getVisitorCount(),
                                    notification.getVisitorDetails().getVisitedType());
                }
            }
        }

        return NotificationItem.builder()
                .notificationId(notification.getId())
                .notificationType(type)
                .subNotificationType(subType)
                .notificationReadStatus(notification.getNotificationReadStatus())
                .notificationOccurTime(DateUtils.formatRelativeTime(notification.getCreatedAt()))
                .notificationDetails(notificationDetails)
                .build();
    }

    public NotificationCountResponse toNotificationCount(
            final long unreadChatCount, final long unreadNotificationCount) {
        return NotificationCountResponse.builder()
                .unreadChatCount(unreadChatCount)
                .unreadNotificationCount(unreadNotificationCount)
                .build();
    }
}
