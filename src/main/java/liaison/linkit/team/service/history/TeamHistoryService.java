package liaison.linkit.team.service.history;

import java.util.List;
import liaison.linkit.team.business.history.TeamHistoryMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.history.TeamHistoryCommandAdapter;
import liaison.linkit.team.implement.history.TeamHistoryQueryAdapter;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamHistoryService {

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamHistoryQueryAdapter teamHistoryQueryAdapter;
    private final TeamHistoryCommandAdapter teamHistoryCommandAdapter;
    private final TeamHistoryMapper teamHistoryMapper;

    @Transactional(readOnly = true)
    public TeamHistoryResponseDTO.TeamHistoryItems getTeamHistoryItems(final Long memberId, final Long teamId) {
        final List<TeamHistory> teamHistories = teamHistoryQueryAdapter.getTeamHistories(teamId);
        return teamHistoryMapper.toTeamHistoryItems(teamHistories);
    }

    public TeamHistoryResponseDTO.TeamHistoryDetail getTeamHistoryDetail(final Long memberId, final Long teamId, final Long teamHistoryId) {
        final TeamHistory teamHistory = teamHistoryQueryAdapter.getTeamHistory(teamHistoryId);
        return teamHistoryMapper.toTeamHistoryDetail(teamHistory);
    }

    public TeamHistoryResponseDTO.AddTeamHistoryResponse addTeamHistory(final Long memberId, final Long teamId, final TeamHistoryRequestDTO.AddTeamHistoryRequest addTeamHistoryRequest) {
        final Team team = teamQueryAdapter.findById(teamId);
        final TeamHistory teamHistory = teamHistoryMapper.toAddTeamHistory(team, addTeamHistoryRequest);
        final TeamHistory savedTeamHistory = teamHistoryCommandAdapter.addTeamHistory(teamHistory);

        if (!team.isTeamHistory()) {
            team.setIsTeamHistory(true);
        }

        return teamHistoryMapper.toAddTeamHistoryResponse(savedTeamHistory);
    }

    public TeamHistoryResponseDTO.UpdateTeamHistoryResponse updateTeamHistory(
            final Long memberId, final Long teamId, final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest) {
        final TeamHistory updatedTeamHistory = teamHistoryCommandAdapter.updateTeamHistory(teamHistoryId, updateTeamHistoryRequest);
        return teamHistoryMapper.toUpdateTeamHistoryResponse(updatedTeamHistory);
    }

    public TeamHistoryResponseDTO.RemoveTeamHistoryResponse removeTeamHistory(final Long memberId, final Long teamId, final Long teamHistoryId) {
        final Team team = teamQueryAdapter.findById(teamId);
        final TeamHistory teamHistory = teamHistoryQueryAdapter.getTeamHistory(teamHistoryId);

        teamHistoryCommandAdapter.removeTeamHistory(teamHistory);
        if (!teamHistoryQueryAdapter.existsByTeamId(team.getId())) {
            team.setIsTeamHistory(false);
        }

        return teamHistoryMapper.toRemoveTeamHistory(teamHistoryId);
    }
}
