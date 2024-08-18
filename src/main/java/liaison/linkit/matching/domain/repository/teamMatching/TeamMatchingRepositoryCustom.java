package liaison.linkit.matching.domain.repository.teamMatching;

import liaison.linkit.matching.domain.TeamMatching;

import java.util.List;

public interface TeamMatchingRepositoryCustom {

    List<TeamMatching> findAllByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds);
    List<TeamMatching> findByMemberIdAndMatchingStatus(final Long memberId);
    List<TeamMatching> findSuccessReceivedMatching(final List<Long> teamMemberAnnouncementIds);
    List<TeamMatching> findSuccessRequestMatching(final Long memberId);

    void deleteByMemberId(final Long memberId);
    void deleteByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds);

    boolean existsByMemberId(final Long memberId);
    boolean existsByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds);
    boolean existsNonCheckByMemberId(final Long memberId, final List<Long> teamMemberAnnouncementIds);
}
