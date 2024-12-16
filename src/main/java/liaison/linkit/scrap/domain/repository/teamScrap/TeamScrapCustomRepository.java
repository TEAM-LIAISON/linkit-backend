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


    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    void deleteByMemberIdAndTeamName(final Long memberId, final String teamName);

    boolean existsByMemberIdAndTeamName(final Long memberId, final String teamName);
}
