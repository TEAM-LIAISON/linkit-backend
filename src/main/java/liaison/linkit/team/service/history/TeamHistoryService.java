package liaison.linkit.team.service.history;

import java.util.List;
import liaison.linkit.team.business.history.TeamHistoryMapper;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.history.TeamHistoryCommandAdapter;
import liaison.linkit.team.implement.history.TeamHistoryQueryAdapter;
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
}
