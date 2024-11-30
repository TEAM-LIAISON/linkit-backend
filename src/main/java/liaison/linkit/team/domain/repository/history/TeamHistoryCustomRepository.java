package liaison.linkit.team.domain.repository.history;

import java.util.List;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;

public interface TeamHistoryCustomRepository {
    List<TeamHistory> getTeamHistories(final Long teamId);

    TeamHistory updateTeamHistory(final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest);

    boolean existsByTeamId(final Long teamId);
}
