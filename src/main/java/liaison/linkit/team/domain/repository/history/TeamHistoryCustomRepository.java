package liaison.linkit.team.domain.repository.history;

import java.util.List;
import liaison.linkit.team.domain.TeamHistory;

public interface TeamHistoryCustomRepository {
    List<TeamHistory> getTeamHistories(final Long teamId);
}
