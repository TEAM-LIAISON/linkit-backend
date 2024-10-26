package liaison.linkit.scrap.implement.teamMemberAnnouncement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.TeamMemberAnnouncementScrap;
import liaison.linkit.scrap.domain.repository.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberAnnouncementScrapCommandAdapter {
    private final TeamMemberAnnouncementScrapRepository teamMemberAnnouncementScrapRepository;


    public TeamMemberAnnouncementScrap create(final TeamMemberAnnouncementScrap teamMemberAnnouncementScrap) {
        return teamMemberAnnouncementScrapRepository.save(teamMemberAnnouncementScrap);
    }

    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {
        teamMemberAnnouncementScrapRepository.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
    }
}
