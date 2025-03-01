package liaison.linkit.search.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import liaison.linkit.search.presentation.dto.CursorRequest;
import liaison.linkit.search.presentation.dto.CursorResponse;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.search.presentation.dto.team.TeamSearchResponseDTO;
import liaison.linkit.team.business.assembler.TeamInformMenuAssembler;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamSearchService {

    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamInformMenuAssembler teamInformMenuAssembler;

    /** 기존 메서드 - 호환성을 위해 유지 필요한 경우 새 API로 마이그레이션 후 제거 가능 */
    public TeamSearchResponseDTO searchTeams(
            final Optional<Long> optionalMemberId,
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final CursorRequest cursorRequest) {
        if (isDefaultSearch(scaleName, cityName, teamStateName)) {
            log.info(
                    "기본 팀 검색 요청: cursor={}, size={}",
                    cursorRequest.getCursor(),
                    cursorRequest.getSize());
            return buildDefaultTeamSearchResponse(optionalMemberId, cursorRequest);
        } else {
            log.info(
                    "팀 필터링 검색 요청: cursor={}, size={}, scaleName={}, cityName={}, teamStateName={}",
                    cursorRequest.getCursor(),
                    cursorRequest.getSize(),
                    scaleName,
                    cityName,
                    teamStateName);
            return buildFilteredTeamSearchResponse(
                    optionalMemberId, scaleName, cityName, teamStateName, cursorRequest);
        }
    }

    /** 주요 팀 목록만 조회하는 메서드 (캐싱 적용) */
    public TeamListResponseDTO getFeaturedTeams(final Optional<Long> optionalMemberId) {

        // 벤처 팀 조회 (최대 4팀)
        Pageable venturePageable = PageRequest.of(0, 4);
        List<Team> ventureTeamEntities =
                teamQueryAdapter.findTopVentureTeams(venturePageable).getContent();
        List<TeamInformMenu> ventureTeamDTOs =
                ventureTeamEntities.stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();
        log.info("ventureTeamDTOs: {}", ventureTeamDTOs);

        // 지원 프로젝트 팀 조회 (최대 4팀)
        Pageable supportPageable = PageRequest.of(0, 4);
        List<Team> supportTeamEntities =
                teamQueryAdapter.findSupportProjectTeams(supportPageable).getContent();
        List<TeamInformMenu> supportTeamDTOs =
                supportTeamEntities.stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();
        log.info("supportTeamDTOs: {}", supportTeamDTOs);

        return TeamListResponseDTO.of(ventureTeamDTOs, supportTeamDTOs);
    }

    /** 커서 기반 팀 검색만 수행하는 메서드 */
    public CursorResponse<TeamInformMenu> searchTeamsWithCursor(
            final Optional<Long> optionalMemberId,
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final CursorRequest cursorRequest) {

        if (isDefaultSearch(scaleName, cityName, teamStateName)) {
            // 벤처 팀과 지원 프로젝트 팀 ID를 제외하고 검색
            List<Long> excludeTeamIds = getExcludeTeamIds();

            CursorResponse<Team> teams =
                    teamQueryAdapter.findAllExcludingIdsWithCursor(excludeTeamIds, cursorRequest);

            return convertTeamsToDTOs(teams, optionalMemberId);
        } else {
            // 필터링 검색
            CursorResponse<Team> teams =
                    teamQueryAdapter.findAllByFilteringWithCursor(
                            scaleName, cityName, teamStateName, cursorRequest);

            return convertTeamsToDTOs(teams, optionalMemberId);
        }
    }

    /** 팀 엔티티를 DTO로 변환하고 커서 응답으로 래핑 */
    private CursorResponse<TeamInformMenu> convertTeamsToDTOs(
            CursorResponse<Team> teams, Optional<Long> optionalMemberId) {
        List<TeamInformMenu> teamDTOs =
                teams.getContent().stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();

        return CursorResponse.of(teamDTOs, teams.getNextCursor());
    }

    /** 제외할 팀 ID 목록 가져오기 (캐싱 적용) */
    public List<Long> getExcludeTeamIds() {
        List<Long> excludeTeamIds = new ArrayList<>();

        // 벤처 팀 ID
        Pageable venturePageable = PageRequest.of(0, 4);
        List<Team> ventureTeams =
                teamQueryAdapter.findTopVentureTeams(venturePageable).getContent();
        excludeTeamIds.addAll(ventureTeams.stream().map(Team::getId).toList());

        // 지원 프로젝트 팀 ID
        Pageable supportPageable = PageRequest.of(0, 4);
        List<Team> supportTeams =
                teamQueryAdapter.findSupportProjectTeams(supportPageable).getContent();
        excludeTeamIds.addAll(supportTeams.stream().map(Team::getId).toList());

        return excludeTeamIds;
    }

    /** 기본 검색 여부를 판단합니다. */
    private boolean isDefaultSearch(
            List<String> scaleName, List<String> cityName, List<String> teamStateName) {
        return (scaleName == null || scaleName.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (teamStateName == null || teamStateName.isEmpty());
    }

    /** 기존 메서드 - 호환성을 위해 유지 */
    private TeamSearchResponseDTO buildDefaultTeamSearchResponse(
            Optional<Long> optionalMemberId, CursorRequest cursorRequest) {
        // 벤처 팀 조회 (최대 4팀)
        Pageable venturePageable = PageRequest.of(0, 4);
        List<Team> ventureTeamEntities =
                teamQueryAdapter.findTopVentureTeams(venturePageable).getContent();
        List<TeamInformMenu> ventureTeamDTOs =
                ventureTeamEntities.stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();

        // 지원 프로젝트 팀 조회 (최대 4팀)
        Pageable supportPageable = PageRequest.of(0, 4);
        List<Team> supportTeamEntities =
                teamQueryAdapter.findSupportProjectTeams(supportPageable).getContent();
        List<TeamInformMenu> supportTeamDTOs =
                supportTeamEntities.stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();

        // 벤처팀과 지원팀의 ID를 제외할 리스트 생성
        List<Long> excludeTeamIds = new ArrayList<>();
        excludeTeamIds.addAll(ventureTeamEntities.stream().map(Team::getId).toList());
        excludeTeamIds.addAll(supportTeamEntities.stream().map(Team::getId).toList());

        log.info("excludeTeamIds: {}", excludeTeamIds);

        // 제외된 팀을 제외한 나머지 팀을 커서 기반으로 조회
        CursorResponse<Team> remainingTeams =
                teamQueryAdapter.findAllExcludingIdsWithCursor(excludeTeamIds, cursorRequest);

        // Team 엔티티를 DTO로 변환
        List<TeamInformMenu> remainingTeamDTOs =
                remainingTeams.getContent().stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();

        // 변환된 DTO로 새로운 CursorResponse 생성
        CursorResponse<TeamInformMenu> cursorResponse =
                CursorResponse.of(remainingTeamDTOs, remainingTeams.getNextCursor());

        return TeamSearchResponseDTO.ofDefault(ventureTeamDTOs, supportTeamDTOs, cursorResponse);
    }

    /** 기존 메서드 - 호환성을 위해 유지 */
    private TeamSearchResponseDTO buildFilteredTeamSearchResponse(
            Optional<Long> optionalMemberId,
            List<String> scaleName,
            List<String> cityName,
            List<String> teamStateName,
            CursorRequest cursorRequest) {
        CursorResponse<Team> teams =
                teamQueryAdapter.findAllByFilteringWithCursor(
                        scaleName, cityName, teamStateName, cursorRequest);

        // Team 엔티티를 DTO로 변환
        List<TeamInformMenu> teamDTOs =
                teams.getContent().stream()
                        .map(
                                team ->
                                        teamInformMenuAssembler.assembleTeamInformMenu(
                                                team, optionalMemberId))
                        .toList();

        // 변환된 DTO로 새로운 CursorResponse 생성
        CursorResponse<TeamInformMenu> cursorResponse =
                CursorResponse.of(teamDTOs, teams.getNextCursor());

        return TeamSearchResponseDTO.ofFiltered(cursorResponse);
    }
}
