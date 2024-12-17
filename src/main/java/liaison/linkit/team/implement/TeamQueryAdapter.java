package liaison.linkit.team.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamCurrentState;
import liaison.linkit.team.domain.repository.currentState.TeamCurrentStateRepository;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.exception.TeamNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamQueryAdapter {

    private final TeamRepository teamRepository;
    private final TeamCurrentStateRepository teamCurrentStateRepository;

    public Team findById(final Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }

    public Team findByTeamName(final String teamName) {
        return teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }

    public List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId) {
        return teamCurrentStateRepository.findTeamCurrentStatesByTeamId(teamId);
    }
}
