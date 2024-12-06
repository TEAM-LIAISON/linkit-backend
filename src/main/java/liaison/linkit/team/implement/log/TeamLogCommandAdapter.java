package liaison.linkit.team.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.repository.log.TeamLogRepository;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;
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

    public void updateTeamLogTypeRepresent(final TeamLog teamLog) {
        teamLogRepository.updateTeamLogTypeRepresent(teamLog);
    }

    public TeamLog updateTeamLogPublicState(final TeamLog teamLog, final boolean isTeamLogCurrentPublicState) {
        return teamLogRepository.updateTeamLogPublicState(teamLog, isTeamLogCurrentPublicState);
    }

    public TeamLog updateTeamLog(final TeamLog teamLog, final UpdateTeamLogRequest updateTeamLogRequest) {
        return teamLogRepository.updateTeamLog(teamLog, updateTeamLogRequest);
    }
}
