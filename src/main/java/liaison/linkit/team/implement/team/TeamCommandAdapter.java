package liaison.linkit.team.implement.team;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamCommandAdapter {
    private final TeamRepository teamRepository;

    public Team add(final Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeam(final String teamCode) {
        teamRepository.deleteTeamByTeamCode(teamCode);
    }
}
