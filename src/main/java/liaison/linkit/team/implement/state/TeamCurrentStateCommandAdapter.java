package liaison.linkit.team.implement.state;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.TeamCurrentState;
import liaison.linkit.team.domain.repository.currentState.TeamCurrentStateRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamCurrentStateCommandAdapter {
    final TeamCurrentStateRepository teamCurrentStateRepository;

    public void saveAll(final List<TeamCurrentState> teamCurrentStates) {
        teamCurrentStateRepository.saveAll(teamCurrentStates);
    }
}
