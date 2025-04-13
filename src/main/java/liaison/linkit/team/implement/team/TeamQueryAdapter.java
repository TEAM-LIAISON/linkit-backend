package liaison.linkit.team.implement.team;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.team.domain.repository.currentState.TeamCurrentStateRepository;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.state.TeamCurrentState;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.exception.team.TeamNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return teamRepository
                .findByTeamCode(teamCode)
                .orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }

    public List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId) {
        return teamCurrentStateRepository.findTeamCurrentStatesByTeamId(teamId);
    }

    public CursorResponse<Team> findAllExcludingIdsWithCursor(
            final List<Long> excludeTeamIds, final CursorRequest cursorRequest) {
        log.debug(
                "커서 기반 팀 조회 요청: excludeTeamIds={}, cursor={}, size={}",
                excludeTeamIds,
                cursorRequest.cursor(),
                cursorRequest.size());
        return teamRepository.findAllExcludingIdsWithCursor(excludeTeamIds, cursorRequest);
    }

    public CursorResponse<Team> findAllByFilteringWithCursor(
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final CursorRequest cursorRequest) {
        log.debug(
                "필터링된 커서 기반 팀 조회 요청: scaleName={}, cityName={}, teamStateName={}, cursor={}, size={}",
                scaleName,
                cityName,
                teamStateName,
                cursorRequest.cursor(),
                cursorRequest.size());
        return teamRepository.findAllByFilteringWithCursor(
                scaleName, cityName, teamStateName, cursorRequest);
    }

    public Page<Team> findAllExcludingIds(
            final List<Long> excludeTeamIds, final Pageable pageable) {
        return teamRepository.findAllExcludingIds(excludeTeamIds, pageable);
    }

    public List<FlatTeamDTO> findHomeTopTeams(final int limit) {
        return teamRepository.findHomeTopTeams(limit);
    }

    public Page<Team> findSupportProjectTeams(final Pageable pageable) {
        return teamRepository.findSupportProjectTeams(pageable);
    }

    public boolean isTeamDeleteInProgress(final String teamCode) {
        return teamRepository.isTeamDeleteInProgress(teamCode);
    }

    public boolean isTeamDeleted(final String teamCode) {
        return teamRepository.isTeamDeleted(teamCode);
    }
}
