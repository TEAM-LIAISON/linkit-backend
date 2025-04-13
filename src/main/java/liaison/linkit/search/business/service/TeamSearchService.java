package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.team.business.assembler.team.TeamInformMenuAssembler;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamSearchService {

    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamInformMenuAssembler teamInformMenuAssembler;
    private final TeamRepository teamRepository;
    private final TeamScrapRepository teamScrapRepository;

    /** 주요 팀 목록만 조회하는 메서드 (캐싱 적용) */
    public TeamListResponseDTO getFeaturedTeams(final Optional<Long> optionalMemberId) {
        // 벤처 팀 조회 (최대 4팀)
        List<TeamInformMenu> topVentureTeamDTOs = getTopVentureTeams(4, optionalMemberId);
        List<TeamInformMenu> topSupportTeamDTOs = getTopSupportTeams(4, optionalMemberId);

        return TeamListResponseDTO.of(topVentureTeamDTOs, topSupportTeamDTOs);
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

    /** 제외할 팀 ID 목록 가져오기 (강제 지정) */
    public List<Long> getExcludeTeamIds() {
        // 큐닷 44L
        // 애프터액션 10L
        // 코지메이커스 36L
        // 독스헌트 AI 4L

        // TFSolution 37L
        // 일기 29L
        // 글들 3L
        // 마인더 19L
        return List.of(56L, 58L, 57L, 36L, 37L, 29L, 3L, 19L);
    }

    /** 기본 검색 여부를 판단합니다. */
    private boolean isDefaultSearch(
            List<String> scaleName, List<String> cityName, List<String> teamStateName) {
        return (scaleName == null || scaleName.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (teamStateName == null || teamStateName.isEmpty());
    }

    private List<TeamInformMenu> getTopVentureTeams(int limit, Optional<Long> memberId) {
        List<FlatTeamDTO> raw = teamRepository.findTopVentureTeams(limit);
        return getTeamInformMenus(limit, memberId, raw);
    }

    private List<TeamInformMenu> getTopSupportTeams(int limit, Optional<Long> memberId) {
        List<FlatTeamDTO> raw = teamRepository.findTopSupportTeams(limit);
        return getTeamInformMenus(limit, memberId, raw);
    }

    @NotNull
    private List<TeamInformMenu> getTeamInformMenus(
            int size, Optional<Long> optionalMemberId, List<FlatTeamDTO> raw) {
        List<Long> teamIds = raw.stream().map(FlatTeamDTO::getTeamId).distinct().toList();
        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        teamScrapRepository.findScrappedTeamIdsByMember(
                                                memberId, teamIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts = teamScrapRepository.countScrapsGroupedByTeam(teamIds);

        List<TeamInformMenu> menus =
                teamInformMenuAssembler.assembleTeamInformMenus(raw, scraps, scrapCounts);

        return menus.size() > size ? menus.subList(0, size) : menus;
    }
}
