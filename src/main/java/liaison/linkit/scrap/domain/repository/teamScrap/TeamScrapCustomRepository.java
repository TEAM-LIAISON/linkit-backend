package liaison.linkit.scrap.domain.repository.teamScrap;

import liaison.linkit.scrap.domain.TeamScrap;

import java.util.List;
import java.util.Optional;

public interface TeamScrapCustomRepository {

    List<TeamScrap> findAllByMemberId(final Long memberId);

    Optional<TeamScrap> findByTeamId(final Long teamId);

    Optional<TeamScrap> findByMemberIdAndTeamId(
            final Long memberId,
            final Long teamId
    );

    boolean existsByTeamIdAndMemberId(final Long teamId, final Long memberId);

    boolean existsByMemberId(final Long memberId);

    boolean existsByTeamId(final Long teamMemberAnnouncementId);

    boolean existsByTeamIds(final List<Long> teamMemberAnnouncementIds);

    void deleteByMemberId(final Long memberId);

    void deleteByMemberIdAndTeamId(final Long memberId, final Long teamMemberAnnouncementId);

    void deleteByTeamId(final Long teamMemberAnnouncementId);

    void deleteByTeamIds(final List<Long> teamMemberAnnouncementIds);

    boolean existsByMemberIdAndTeamId(final Long memberId, final Long teamId);
}
