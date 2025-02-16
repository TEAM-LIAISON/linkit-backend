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
        final Boolean isAnnouncement,
        final List<String> cityName,
        final List<String> teamStateName,
        final Pageable pageable
    ) {
        // 쿼리 파라미터가 모두 비어있는 경우: 기본 검색
        boolean isDefaultSearch = (scaleName == null || scaleName.isEmpty())
            && (!isAnnouncement)
            && (cityName == null || cityName.isEmpty())
            && (teamStateName == null || teamStateName.isEmpty());

        if (isDefaultSearch) {
            Pageable venturePageable = PageRequest.of(0, 4);
            List<Team> ventureTeams = teamQueryAdapter.findTopVentureTeams(venturePageable).getContent();

            List<TeamInformMenu> ventureTeamDTOs = ventureTeams.stream()
                .map(team -> teamInformMenuAssembler.assembleTeamInformMenu(team, optionalMemberId))
                .toList();

            Pageable supportPageable = PageRequest.of(0, 4);
            List<Team> supportTeams = teamQueryAdapter.findSupportProjectTeams(supportPageable).getContent();

            List<TeamInformMenu> supportTeamDTOs = supportTeams.stream()
                .map(team -> teamInformMenuAssembler.assembleTeamInformMenu(team, optionalMemberId))
                .toList();

            // 제외할 팀 ID 리스트 생성
            List<Long> excludeTeamIds = new ArrayList<>();

            // 벤처 팀 ID 추가
            excludeTeamIds.addAll(ventureTeams.stream()
                .map(Team::getId)
                .toList());

            // 지원 프로젝트 팀 ID 추가
            excludeTeamIds.addAll(supportTeams.stream()
                .map(Team::getId)
                .toList());

            Page<Team> remainingTeams = teamQueryAdapter.findAllExcludingIds(excludeTeamIds, pageable);
            Page<TeamInformMenu> remainingTeamDTOs = remainingTeams.map(team ->
                teamInformMenuAssembler.assembleTeamInformMenu(team, optionalMemberId)
            );

            return TeamSearchResponseDTO.builder()
                .ventureTeams(ventureTeamDTOs)
                .supportProjectTeams(supportTeamDTOs)
                .defaultTeams(remainingTeamDTOs)
                .build();
        } else {
            Page<Team> teams = teamQueryAdapter.findAllByFiltering(scaleName, isAnnouncement, cityName, teamStateName, pageable);
            Page<TeamInformMenu> teamDTOs = teams.map(team ->
                teamInformMenuAssembler.assembleTeamInformMenu(team, optionalMemberId)
            );

            return TeamSearchResponseDTO.builder()
                .ventureTeams(Collections.emptyList())
                .supportProjectTeams(Collections.emptyList())
                .defaultTeams(teamDTOs)
                .build();
        }
    }
}
