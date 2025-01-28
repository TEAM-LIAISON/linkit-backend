package liaison.linkit.scrap.implement.teamScrap;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapCommandAdapter {
    private final TeamScrapRepository teamScrapRepository;

    public TeamScrap addTeamScrap(final TeamScrap teamScrap) {
        return teamScrapRepository.save(teamScrap);
    }

    public void delete(final TeamScrap teamScrap) {
        teamScrapRepository.delete(teamScrap);
    }

    public void deleteByMemberIdAndTeamCode(final Long memberId, final String teamCode) {
        teamScrapRepository.deleteByMemberIdAndTeamCode(memberId, teamCode);
    }

    public void deleteAllByMemberId(final Long memberId) {
        teamScrapRepository.deleteAllByMemberId(memberId);
    }

    public void deleteAllByTeamIds(final List<Long> teamIds) {
        teamScrapRepository.deleteAllByTeamIds(teamIds);
    }

    public void deleteAllByTeamId(final Long teamId) {
        teamScrapRepository.deleteAllByTeamId(teamId);
    }
}
