package liaison.linkit.team.domain.repository.log;

import java.util.List;
import java.util.Optional;

import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogDynamicResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;

public interface TeamLogCustomRepository {
    List<TeamLog> getTeamLogs(final Long teamId);

    List<TeamLog> getTeamLogsPublic(final Long teamId);

    void updateTeamLogTypeRepresent(final TeamLog teamLog);

    void updateTeamLogTypeGeneral(final TeamLog teamLog);

    Optional<TeamLog> findRepresentativeTeamLog(final Long teamId);

    Optional<TeamLog> findRepresentativePublicTeamLog(final Long teamId);

    TeamLog updateTeamLogPublicState(
            final TeamLog teamLog, final boolean isTeamLogCurrentPublicState);

    boolean existsRepresentativeTeamLogByTeam(final Long teamId);

    boolean existsRepresentativePublicTeamLogByTeam(final Long teamId);

    TeamLog updateTeamLog(final TeamLog teamLog, final UpdateTeamLogRequest updateTeamLogRequest);

    List<TeamLog> findTopView(final int limit);

    void deleteAllTeamLogs(final Long teamId);

    List<TeamLogDynamicResponse> findAllDynamicVariablesWithTeamLog();

    TeamLog getRandomTeamLog();
}
