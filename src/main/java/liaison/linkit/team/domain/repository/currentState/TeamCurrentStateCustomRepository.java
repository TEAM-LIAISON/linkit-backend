package liaison.linkit.team.domain.repository.currentState;

import java.util.List;
import liaison.linkit.team.domain.TeamCurrentState;

public interface TeamCurrentStateCustomRepository {

    List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId);
}
