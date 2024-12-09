package liaison.linkit.team.domain.repository.currentState;

import java.util.Optional;
import liaison.linkit.team.domain.state.TeamState;

public interface TeamStateCustomRepository {

    Optional<TeamState> findByStateName(final String stateName);
}
