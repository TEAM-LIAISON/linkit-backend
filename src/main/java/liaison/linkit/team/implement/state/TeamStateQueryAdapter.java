package liaison.linkit.team.implement.state;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.currentState.TeamStateRepository;
import liaison.linkit.team.domain.state.TeamState;
import liaison.linkit.team.exception.state.TeamStateNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamStateQueryAdapter {
    private final TeamStateRepository teamStateRepository;

    public TeamState findByStateName(final String stateName) {
        return teamStateRepository
                .findByStateName(stateName)
                .orElseThrow(() -> TeamStateNotFoundException.EXCEPTION);
    }
}
