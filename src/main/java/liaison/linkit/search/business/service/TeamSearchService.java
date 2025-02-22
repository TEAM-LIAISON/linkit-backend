package liaison.linkit.search.business.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import liaison.linkit.search.presentation.dto.TeamSearchResponseDTO;
import liaison.linkit.team.business.assembler.TeamInformMenuAssembler;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    public TeamSearchResponseDTO searchTeams(
            final Optional<Long> optionalMemberId,
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final Pageable pageable) {
        if (isDefaultSearch(scaleName, cityName, teamStateName)) {
            return buildDefaultTeamSearchResponse(optionalMemberId, pageable);
        } else {
            return buildFilteredTeamSearchResponse(
                    optionalMemberId, scaleName, cityName, teamStateName, pageable);
        }
    }

    /** 기본 검색 여부를 판단합니다. */
    private boolean isDefaultSearch(
            List<String> scaleName, List<String> cityName, List<String> teamStateName) {
        return (scaleName == null || scaleName.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (teamStateName == null || teamStateName.isEmpty());
    }

    /** 기본 검색일 경우의 응답 DTO를 구성합니다. - 벤처 팀과 지원 프로젝트 팀을 조회하고, 해당 팀 ID들을 제외한 나머지 팀을 페이지네이션합니다. */
    private TeamSearchResponseDTO buildDefaultTeamSearchResponse(
            Optional<Long> optionalMemberId, Pageable pageable) {
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

        // 제외된 팀을 제외한 나머지 팀을 페이지네이션하여 조회
        Page<Team> remainingTeams = teamQueryAdapter.findAllExcludingIds(excludeTeamIds, pageable);
        Page<TeamInformMenu> remainingTeamDTOs =
                remainingTeams.map(
                        team ->
                                teamInformMenuAssembler.assembleTeamInformMenu(
                                        team, optionalMemberId));

        return TeamSearchResponseDTO.builder()
                .ventureTeams(ventureTeamDTOs)
                .supportProjectTeams(supportTeamDTOs)
                .defaultTeams(remainingTeamDTOs)
                .build();
    }

    /** 필터링 조건이 있는 경우의 응답 DTO를 구성합니다. */
    private TeamSearchResponseDTO buildFilteredTeamSearchResponse(
            Optional<Long> optionalMemberId,
            List<String> scaleName,
            List<String> cityName,
            List<String> teamStateName,
            Pageable pageable) {
        Page<Team> teams =
                teamQueryAdapter.findAllByFiltering(scaleName, cityName, teamStateName, pageable);
        Page<TeamInformMenu> teamDTOs =
                teams.map(
                        team ->
                                teamInformMenuAssembler.assembleTeamInformMenu(
                                        team, optionalMemberId));

        return TeamSearchResponseDTO.builder()
                .ventureTeams(Collections.emptyList())
                .supportProjectTeams(Collections.emptyList())
                .defaultTeams(teamDTOs)
                .build();
    }
}
