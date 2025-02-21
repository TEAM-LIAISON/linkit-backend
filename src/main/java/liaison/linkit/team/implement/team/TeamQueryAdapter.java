package liaison.linkit.team.implement.team;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.currentState.TeamCurrentStateRepository;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.state.TeamCurrentState;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.exception.team.TeamNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class TeamQueryAdapter {

    private final TeamRepository teamRepository;
    private final TeamCurrentStateRepository teamCurrentStateRepository;

    public boolean existsByTeamCode(final String teamCode) {
        return teamRepository.existsByTeamCode(teamCode);
    }

    public Team findById(final Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }

    public Team findByTeamCode(final String teamCode) {
        return teamRepository.findByTeamCode(teamCode)
            .orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }

    public List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId) {
        return teamCurrentStateRepository.findTeamCurrentStatesByTeamId(teamId);
    }

    public Page<Team> findAllByFiltering(
        List<String> scaleName,
        List<String> cityName,
        List<String> teamStateName,
        Pageable pageable
    ) {
        log.info("팀 필터링 요청 발생");
        return teamRepository.findAllByFiltering(scaleName, cityName, teamStateName, pageable);
    }

    @Cacheable(
        value = "topVentureTeams",
        key = "'topVentureTeams'"  // 상수 키를 사용
    )
    public Page<Team> findTopVentureTeams(
        final Pageable pageable
    ) {
        return teamRepository.findTopVentureTeams(pageable);
    }

    @Cacheable(
        value = "supportProjectTeams",
        key = "'supportProjectTeams'"  // 상수 키를 사용
    )
    public Page<Team> findSupportProjectTeams(
        final Pageable pageable
    ) {
        return teamRepository.findSupportProjectTeams(pageable);
    }

    public Page<Team> findAllExcludingIds(
        final List<Long> excludeTeamIds,
        final Pageable pageable
    ) {
        return teamRepository.findAllExcludingIds(excludeTeamIds, pageable);
    }

    @Cacheable(
        value = "homeTopTeams",
        key = "'homeTopTeams'"  // 상수 키를 사용
    )
    public List<Team> findHomeTopTeams(final int limit) {
        return teamRepository.findHomeTopTeams(limit);
    }

    public boolean isTeamDeleteInProgress(final String teamCode) {
        return teamRepository.isTeamDeleteInProgress(teamCode);
    }

    public boolean isTeamDeleted(final String teamCode) {
        return teamRepository.isTeamDeleted(teamCode);
    }
}
