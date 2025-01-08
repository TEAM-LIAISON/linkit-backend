package liaison.linkit.scrap.domain.repository.teamScrap;

import java.util.List;
import liaison.linkit.scrap.domain.TeamScrap;

public interface TeamScrapCustomRepository {

    List<TeamScrap> findAllByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    void deleteByMemberIdAndTeamCode(final Long memberId, final String teamCode);

    boolean existsByMemberIdAndTeamCode(final Long memberId, final String teamCode);

    int countTotalTeamScrapByTeamCode(final String teamCode);
}
