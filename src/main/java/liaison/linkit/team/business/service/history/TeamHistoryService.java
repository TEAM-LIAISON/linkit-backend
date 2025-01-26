package liaison.linkit.team.business.service.history;

import java.util.List;
import liaison.linkit.team.business.mapper.history.TeamHistoryMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.history.TeamHistory;
import liaison.linkit.team.exception.teamMember.TeamAdminNotRegisteredException;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.history.TeamHistoryCommandAdapter;
import liaison.linkit.team.implement.history.TeamHistoryQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
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
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    @Transactional(readOnly = true)
    public TeamHistoryResponseDTO.TeamHistoryCalendarResponse getTeamHistoryCalendarResponses(final String teamCode) {
        final List<TeamHistory> teamHistories = teamHistoryQueryAdapter.getTeamHistories(teamCode);
        return teamHistoryMapper.toTeamHistoryCalendar(teamHistories);
    }

    @Transactional(readOnly = true)
    public TeamHistoryResponseDTO.TeamHistoryItems getTeamHistoryItems(final Long memberId, final String teamCode) {
        final List<TeamHistory> teamHistories = teamHistoryQueryAdapter.getTeamHistories(teamCode);
        return teamHistoryMapper.toTeamHistoryItems(teamHistories);
    }

    @Transactional(readOnly = true)
    public TeamHistoryResponseDTO.TeamHistoryDetail getTeamHistoryDetail(final Long memberId, final String teamCode, final Long teamHistoryId) {
        final TeamHistory teamHistory = teamHistoryQueryAdapter.getTeamHistory(teamHistoryId);
        return teamHistoryMapper.toTeamHistoryDetail(teamHistory);
    }

    public TeamHistoryResponseDTO.AddTeamHistoryResponse addTeamHistory(final Long memberId, final String teamCode, final TeamHistoryRequestDTO.AddTeamHistoryRequest addTeamHistoryRequest) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        log.info("team ={}", team);
        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(team.getId(), memberId)) {
            throw TeamAdminNotRegisteredException.EXCEPTION;
        }
        final TeamHistory teamHistory = teamHistoryMapper.toAddTeamHistory(team, addTeamHistoryRequest);
        log.info("teamHistory ={}", teamHistory);
        final TeamHistory savedTeamHistory = teamHistoryCommandAdapter.addTeamHistory(teamHistory);
        log.info("savedTeamHistory ={}", savedTeamHistory);

        return teamHistoryMapper.toAddTeamHistoryResponse(savedTeamHistory);
    }

    public TeamHistoryResponseDTO.UpdateTeamHistoryResponse updateTeamHistory(
            final Long memberId, final String teamCode, final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest
    ) {
        final TeamHistory updatedTeamHistory = teamHistoryCommandAdapter.updateTeamHistory(teamHistoryId, updateTeamHistoryRequest);
        return teamHistoryMapper.toUpdateTeamHistoryResponse(updatedTeamHistory);
    }

    public TeamHistoryResponseDTO.RemoveTeamHistoryResponse removeTeamHistory(final Long memberId, final String teamCode, final Long teamHistoryId) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        final TeamHistory teamHistory = teamHistoryQueryAdapter.getTeamHistory(teamHistoryId);

        teamHistoryCommandAdapter.removeTeamHistory(teamHistory);
        return teamHistoryMapper.toRemoveTeamHistory(teamHistoryId);
    }
}
