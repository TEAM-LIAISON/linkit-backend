package liaison.linkit.scrap.implement.announcementScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementScrapCommandAdapter {
    private final AnnouncementScrapRepository teamMemberAnnouncementScrapRepository;
    
    public AnnouncementScrap addAnnouncementScrap(final AnnouncementScrap announcementScrap) {
        return teamMemberAnnouncementScrapRepository.save(announcementScrap);
    }

    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {
        teamMemberAnnouncementScrapRepository.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
    }
}
