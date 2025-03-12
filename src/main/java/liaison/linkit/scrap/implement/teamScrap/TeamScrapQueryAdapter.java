package liaison.linkit.scrap.implement.teamScrap;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapQueryAdapter {
    private final TeamScrapRepository teamScrapRepository;

    public List<TeamScrap> findAllByMemberId(final Long memberId) {
        return teamScrapRepository.findAllByMemberId(memberId);
    }

    public boolean existsByMemberIdAndTeamCode(final Long memberId, final String teamCode) {
        return teamScrapRepository.existsByMemberIdAndTeamCode(memberId, teamCode);
    }

    public int countTotalTeamScrapByTeamCode(final String teamCode) {
        return teamScrapRepository.countTotalTeamScrapByTeamCode(teamCode);
    }
}
