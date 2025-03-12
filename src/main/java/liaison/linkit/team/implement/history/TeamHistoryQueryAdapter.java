package liaison.linkit.team.implement.history;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.history.TeamHistory;
import liaison.linkit.team.domain.repository.history.TeamHistoryRepository;
import liaison.linkit.team.exception.history.TeamHistoryNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamHistoryQueryAdapter {

    private final TeamHistoryRepository teamHistoryRepository;

    public List<TeamHistory> getTeamHistories(final String teamCode) {
        return teamHistoryRepository.getTeamHistories(teamCode);
    }

    public TeamHistory getTeamHistory(final Long teamHistoryId) {
        return teamHistoryRepository
                .findById(teamHistoryId)
                .orElseThrow(() -> TeamHistoryNotFoundException.EXCEPTION);
    }
}
