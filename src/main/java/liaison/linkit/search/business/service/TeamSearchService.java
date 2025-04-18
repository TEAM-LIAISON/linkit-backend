package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.search.business.model.TeamSearchCondition;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.team.business.assembler.team.TeamInformMenuAssembler;
import liaison.linkit.team.domain.repository.team.TeamRepository;
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

    private static final List<Long> DEFAULT_EXCLUDE_TEAM_IDS =
            List.of(61L, 59L, 58L, 57L, 37L, 29L, 3L, 19L);

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
            final TeamSearchCondition condition,
            final CursorRequest cursorRequest) {

        if (condition.isDefault()) {
            // /api/v1/team/search?size=20
            if (!cursorRequest.hasNext()) {
                List<TeamInformMenu> results =
                        getFirstPageDefaultTeams(cursorRequest.size(), optionalMemberId);
                String nextCursor =
                        results.isEmpty() ? null : results.get(results.size() - 1).getTeamCode();
                return CursorResponse.of(results, nextCursor);
            }
            // /api/v1/team/search?cursor={emailId}&size=20
            List<TeamInformMenu> results =
                    getAllTeamsWithoutFilter(cursorRequest.size(), optionalMemberId, cursorRequest);
            String nextCursor =
                    results.isEmpty() ? null : results.get(results.size() - 1).getTeamCode();
            return CursorResponse.of(results, nextCursor);
        }

        // 필터가 포함된 경우
        List<TeamInformMenu> results =
                getAllProfilesWithFilter(
                        cursorRequest.size(), optionalMemberId, condition, cursorRequest);
        String nextCursor =
                results.isEmpty() ? null : results.get(results.size() - 1).getTeamCode();
        return CursorResponse.of(results, nextCursor);
    }

    private List<TeamInformMenu> getTopVentureTeams(int limit, Optional<Long> memberId) {
        List<FlatTeamDTO> raw = teamRepository.findTopVentureTeams(limit);
        return getTeamInformMenus(limit, memberId, raw);
    }

    private List<TeamInformMenu> getTopSupportTeams(int limit, Optional<Long> memberId) {
        List<FlatTeamDTO> raw = teamRepository.findTopSupportTeams(limit);
        return getTeamInformMenus(limit, memberId, raw);
    }

    private List<TeamInformMenu> getFirstPageDefaultTeams(int size, Optional<Long> memberId) {
        List<FlatTeamDTO> raw =
                teamRepository.findFlatTeamsWithoutCursor(DEFAULT_EXCLUDE_TEAM_IDS, size);
        return getTeamInformMenus(size, memberId, raw);
    }

    private List<TeamInformMenu> getAllTeamsWithoutFilter(
            int size, Optional<Long> memberId, CursorRequest cursorRequest) {
        List<FlatTeamDTO> raw =
                teamRepository.findAllTeamsWithoutFilter(DEFAULT_EXCLUDE_TEAM_IDS, cursorRequest);
        return getTeamInformMenus(size, memberId, raw);
    }

    private List<TeamInformMenu> getAllProfilesWithFilter(
            int size,
            Optional<Long> optionalMemberId,
            TeamSearchCondition condition,
            CursorRequest cursorRequest) {
        List<FlatTeamDTO> raw =
                teamRepository.findFilteredFlatTeamsWithCursor(
                        condition.scaleName(),
                        condition.cityName(),
                        condition.teamStateName(),
                        cursorRequest);
        return getTeamInformMenus(size, optionalMemberId, raw);
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
