package liaison.linkit.scrap.domain.repository.announcementScrap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import liaison.linkit.scrap.domain.AnnouncementScrap;

public interface AnnouncementScrapCustomRepository {
    void deleteByMemberIdAndTeamMemberAnnouncementId(
            final Long memberId, final Long teamMemberAnnouncementId);

    boolean existsByMemberIdAndTeamMemberAnnouncementId(
            final Long memberId, final Long teamMemberAnnouncementId);

    int getTotalAnnouncementScrapCount(final Long teamMemberAnnouncementId);

    List<AnnouncementScrap> findAllByMemberId(final Long memberId);

    void deleteAllByMemberId(final Long memberId);

    void deleteAllByAnnouncementIds(final List<Long> teamMemberAnnouncementIds);

    Set<Long> findScrappedAnnouncementIdsByMember(Long memberId, List<Long> announcementIds);

    Map<Long, Integer> countScrapsGroupedByAnnouncement(List<Long> announcementIds);
}
