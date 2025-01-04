package liaison.linkit.team.domain.repository.log;

import java.util.List;
import java.util.Optional;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;

public interface TeamLogCustomRepository {
    List<TeamLog> getTeamLogs(final Long teamId);

    List<TeamLog> getTeamLogsPublic(final Long teamId);

    void updateTeamLogTypeRepresent(final TeamLog teamLog);

    Optional<TeamLog> findRepresentativeTeamLog(final Long teamId);

    TeamLog updateTeamLogPublicState(final TeamLog teamLog, final boolean isTeamLogCurrentPublicState);

    boolean existsRepresentativeTeamLogByTeam(final Long teamId);

    TeamLog updateTeamLog(final TeamLog teamLog, final UpdateTeamLogRequest updateTeamLogRequest);
}
