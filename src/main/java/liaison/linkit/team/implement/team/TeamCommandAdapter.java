package liaison.linkit.team.implement.team;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.team.type.TeamStatus;
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

    public Team updateTeamStatus(final TeamStatus teamStatus, final String teamCode) {
        return teamRepository.updateTeamStatus(teamStatus, teamCode);
    }

    public void updateTeam(final Team team) {
        teamRepository.updateTeam(team);
    }
}
