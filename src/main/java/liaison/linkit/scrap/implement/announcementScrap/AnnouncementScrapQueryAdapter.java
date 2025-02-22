package liaison.linkit.scrap.implement.announcementScrap;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementScrapQueryAdapter {
    private final AnnouncementScrapRepository teamMemberAnnouncementScrapRepository;

    public boolean existsByMemberIdAndTeamMemberAnnouncementId(
            final Long memberId, final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementScrapRepository.existsByMemberIdAndTeamMemberAnnouncementId(
                memberId, teamMemberAnnouncementId);
    }

    public int getTotalAnnouncementScrapCount(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementScrapRepository.getTotalAnnouncementScrapCount(
                teamMemberAnnouncementId);
    }

    public List<AnnouncementScrap> findAllByMemberId(final Long memberId) {
        return teamMemberAnnouncementScrapRepository.findAllByMemberId(memberId);
    }
}
