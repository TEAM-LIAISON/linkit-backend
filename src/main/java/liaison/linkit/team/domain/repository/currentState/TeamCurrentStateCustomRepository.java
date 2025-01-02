package liaison.linkit.team.domain.repository.currentState;

import java.util.List;
import liaison.linkit.team.domain.state.TeamCurrentState;

public interface TeamCurrentStateCustomRepository {

    List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId);

    boolean existsTeamCurrentStatesByTeamId(final Long teamId);

    void deleteAllByTeamId(final Long teamId);
}
