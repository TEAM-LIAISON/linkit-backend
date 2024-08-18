package liaison.linkit.wish.domain.repository.teamWish;

import liaison.linkit.wish.domain.TeamWish;

import java.util.List;
import java.util.Optional;

public interface TeamWishRepositoryCustom {

    List<TeamWish> findAllByMemberId(final Long memberId);
    Optional<TeamWish> findByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
    Optional<TeamWish> findByMemberIdAndTeamMemberAnnouncementId(final Long teamMemberAnnouncementId, final Long memberId);
    boolean findByTeamMemberAnnouncementIdAndMemberId(final Long teamMemberAnnouncementId, final Long memberId);

    boolean existsByMemberId(final Long memberId);
    boolean existsByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
    boolean existsByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds);

    void deleteByMemberId(final Long memberId);
    void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId);
    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
    void deleteByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds);
}
