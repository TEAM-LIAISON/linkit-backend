package liaison.linkit.team.domain.repository.history;

import java.util.List;
import liaison.linkit.team.domain.history.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;

public interface TeamHistoryCustomRepository {
    List<TeamHistory> getTeamHistories(final String teamCode);

    TeamHistory updateTeamHistory(final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest);
}
