package liaison.linkit.team.implement.teamMemberAnnouncement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.exception.teamMemberAnnouncement.TeamMemberAnnouncementNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberAnnouncementQueryAdapter {
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    public TeamMemberAnnouncement findById(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> TeamMemberAnnouncementNotFoundException.EXCEPTION);
    }


}
