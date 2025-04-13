package liaison.linkit.team.domain.repository.team;

import java.util.List;
import java.util.Optional;

import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import liaison.linkit.team.presentation.team.dto.TeamDynamicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamCustomRepository {

    Optional<Team> findByTeamCode(final String teamCode);

    boolean existsByTeamCode(final String teamCode);

    void deleteTeamByTeamCode(final String teamCode);

    List<FlatTeamDTO> findHomeTopTeams(final int size);

    Page<Team> findSupportProjectTeams(final Pageable pageable);

    Page<Team> findAllExcludingIds(final List<Long> excludeTeamIds, final Pageable pageable);

    Team updateTeamStatus(final TeamStatus teamStatus, final String teamCode);

    void updateTeam(final Team team);

    boolean isTeamDeleteInProgress(final String teamCode);

    boolean isTeamDeleted(final String teamCode);

    CursorResponse<Team> findAllExcludingIdsWithCursor(
            List<Long> excludeTeamIds, CursorRequest cursorRequest);

    CursorResponse<Team> findAllByFilteringWithCursor(
            List<String> scaleName,
            List<String> cityName,
            List<String> teamStateName,
            CursorRequest cursorRequest);

    List<TeamDynamicResponse> findAllDynamicVariablesWithTeam();

    List<FlatTeamDTO> findTopVentureTeams(int limit);

    List<FlatTeamDTO> findTopSupportTeams(int limit);
}
