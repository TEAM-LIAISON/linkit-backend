package liaison.linkit.team.domain.repository.team;

import java.util.List;
import java.util.Optional;

import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamCustomRepository {

    Optional<Team> findByTeamCode(final String teamCode);

    boolean existsByTeamCode(final String teamCode);

    Page<Team> findAllByFiltering(
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final Pageable pageable);

    void deleteTeamByTeamCode(final String teamCode);

    List<Team> findHomeTopTeams(final int limit);

    Page<Team> findTopVentureTeams(final Pageable pageable);

    Page<Team> findSupportProjectTeams(final Pageable pageable);

    Page<Team> findAllExcludingIds(final List<Long> excludeTeamIds, final Pageable pageable);

    Team updateTeamStatus(final TeamStatus teamStatus, final String teamCode);

    void updateTeam(final Team team);

    boolean isTeamDeleteInProgress(final String teamCode);

    boolean isTeamDeleted(final String teamCode);
}
