package liaison.linkit.team.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.repository.log.TeamLogRepository;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogCommandAdapter {
    private final TeamLogRepository teamLogRepository;

    public TeamLog addTeamLog(final TeamLog teamLog) {
        return teamLogRepository.save(teamLog);
    }

    public void remove(final TeamLog teamLog) {
        teamLogRepository.delete(teamLog);
    }

    public TeamLog updateTeamLogType(final TeamLog teamLog, final TeamLogRequestDTO.UpdateTeamLogType updateTeamLogType) {
        return teamLogRepository.updateTeamLogType(teamLog, updateTeamLogType);
    }
}
