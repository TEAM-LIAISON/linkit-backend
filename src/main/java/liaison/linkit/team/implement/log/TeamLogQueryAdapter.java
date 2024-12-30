package liaison.linkit.team.implement.log;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.repository.log.TeamLogRepository;
import liaison.linkit.team.exception.log.TeamLogNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogQueryAdapter {
    private final TeamLogRepository teamLogRepository;

    public TeamLog getTeamLog(final Long teamLogId) {
        return teamLogRepository.findById(teamLogId)
                .orElseThrow(() -> TeamLogNotFoundException.EXCEPTION);
    }

    public List<TeamLog> getTeamLogs(final Long teamId) {
        return teamLogRepository.getTeamLogs(teamId);
    }

    public List<TeamLog> getTeamLogsPublic(final Long teamId) {
        return teamLogRepository.getTeamLogsPublic(teamId);
    }

    public TeamLog getRepresentativeTeamLog(final Long teamId) {
        return teamLogRepository.findRepresentativeTeamLog(teamId)
                .orElseThrow(() -> TeamLogNotFoundException.EXCEPTION);
    }

    public boolean existingRepresentativeTeamLogByTeam(final Long teamId) {
        return teamLogRepository.existingRepresentativeTeamLogByTeam(teamId);
    }

}
