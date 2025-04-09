package liaison.linkit.scrap.domain.repository.teamScrap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import liaison.linkit.scrap.domain.TeamScrap;

public interface TeamScrapCustomRepository {

    List<TeamScrap> findAllByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberIdAndTeamCode(final Long memberId, final String teamCode);

    boolean existsByMemberIdAndTeamCode(final Long memberId, final String teamCode);

    int countTotalTeamScrapByTeamCode(final String teamCode);

    /*
    [DELETE]
     */
    void deleteAllByMemberId(final Long memberId);

    void deleteAllByTeamIds(final List<Long> teamIds);

    void deleteAllByTeamId(final Long teamId);

    Set<Long> findScrappedTeamIdsByMember(Long memberId, List<Long> teamIds);

    Map<Long, Integer> countScrapsGroupedByTeam(List<Long> teamIds);
}
