package liaison.linkit.team.implement.announcement;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberAnnouncementCommandAdapter {
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    public TeamMemberAnnouncement addTeamMemberAnnouncement(
            final TeamMemberAnnouncement teamMemberAnnouncement) {
        return teamMemberAnnouncementRepository.save(teamMemberAnnouncement);
    }

    public TeamMemberAnnouncement updateTeamMemberAnnouncement(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest) {
        return teamMemberAnnouncementRepository.updateTeamMemberAnnouncement(
                teamMemberAnnouncement, updateTeamMemberAnnouncementRequest);
    }

    public void removeTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement) {
        teamMemberAnnouncementRepository.delete(teamMemberAnnouncement);
    }

    public TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final boolean isTeamMemberAnnouncementCurrentPublicState) {
        return teamMemberAnnouncementRepository.updateTeamMemberAnnouncementPublicState(
                teamMemberAnnouncement, isTeamMemberAnnouncementCurrentPublicState);
    }

    public void deleteAllByIds(List<Long> announcementIds) {
        teamMemberAnnouncementRepository.deleteAllByIds(announcementIds);
    }

    public TeamMemberAnnouncement updateTeamMemberAnnouncementClosedState(final TeamMemberAnnouncement teamMemberAnnouncement,
                                                                          final boolean isTeamMemberAnnouncementInProgress) {
        return teamMemberAnnouncementRepository.updateTeamMemberAnnouncementClosedState(
                teamMemberAnnouncement, isTeamMemberAnnouncementInProgress);
    }
}
