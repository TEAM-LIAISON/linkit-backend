package liaison.linkit.team.domain.repository.announcement;

import java.util.List;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;

public interface TeamMemberAnnouncementCustomRepository {
    List<TeamMemberAnnouncement> getTeamMemberAnnouncements(final Long teamId);

    TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId);

    TeamMemberAnnouncement updateTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement, final UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest);

    TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(final TeamMemberAnnouncement teamMemberAnnouncement, final boolean isTeamMemberAnnouncementCurrentPublicState);
}
