package liaison.linkit.team.domain.repository.log;

import java.util.List;
import java.util.Optional;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogType;

public interface TeamLogCustomRepository {
    List<TeamLog> getTeamLogs(final Long teamId);

    TeamLog updateTeamLogType(final TeamLog teamLog, final UpdateTeamLogType updateTeamLogType);

    Optional<TeamLog> findRepresentativeTeamLog(final Long teamId);

    boolean existsTeamLogByTeamId(final Long teamId);
}
