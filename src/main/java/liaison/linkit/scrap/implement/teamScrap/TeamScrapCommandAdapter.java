package liaison.linkit.scrap.implement.teamScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapCommandAdapter {
    private TeamScrapRepository teamScrapRepository;

    public TeamScrap addTeamScrap(final TeamScrap teamScrap) {
        return teamScrapRepository.save(teamScrap);
    }

    public void delete(final TeamScrap teamScrap) {
        teamScrapRepository.delete(teamScrap);
    }

    public void deleteByMemberIdAndTeamCode(final Long memberId, final String teamCode) {
        teamScrapRepository.deleteByMemberIdAndTeamCode(memberId, teamCode);
    }
}
