package liaison.linkit.team.implement.history;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.domain.repository.history.TeamHistoryRepository;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamHistoryCommandAdapter {

    private TeamHistoryRepository teamHistoryRepository;

    public TeamHistory addTeamHistory(final TeamHistory teamHistory) {
        return teamHistoryRepository.save(teamHistory);
    }

    public TeamHistory updateTeamHistory(final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest) {
        return teamHistoryRepository.updateTeamHistory(teamHistoryId, updateTeamHistoryRequest);
    }

    public void removeTeamHistory(final TeamHistory teamHistory) {
        teamHistoryRepository.delete(teamHistory);
    }
}
