package liaison.linkit.team.implement.state;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.currentState.TeamCurrentStateRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamCurrentStateQueryAdapter {
    final TeamCurrentStateRepository teamCurrentStateRepository;

    public boolean existsTeamCurrentStatesByTeamId(final Long teamId) {
        return teamCurrentStateRepository.existsTeamCurrentStatesByTeamId(teamId);
    }
}
