package liaison.linkit.scrap.implement.announcementScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementScrapQueryAdapter {
    private final AnnouncementScrapRepository teamMemberAnnouncementScrapRepository;

    public boolean existsByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementScrapRepository.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
    }
}
