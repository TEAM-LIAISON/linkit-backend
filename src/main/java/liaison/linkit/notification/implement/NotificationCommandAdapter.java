package liaison.linkit.notification.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.team.domain.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandAdapter {
    private final NotificationRepository notificationRepository;

    public void addInvitationNotificationsForTeams(final Member member, final List<Team> teams) {
        List<Notification> notifications = teams.stream()
                .map(team -> Notification.builder()
                        .id(UUID.randomUUID().toString())
                        .memberId(member.getId().toString())
                        .notificationType(NotificationType.TEAM_INVITATION)
                        .notificationStatus(NotificationStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .invitationDetails(Notification.InvitationDetails.builder()
                                .teamName(team.getTeamName())
                                .build()
                        )
                        .build()
                ).toList();
        notificationRepository.saveAll(notifications);
    }
}
