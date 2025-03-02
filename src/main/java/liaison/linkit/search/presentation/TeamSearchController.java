package liaison.linkit.search.presentation;

import java.util.List;
import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.search.presentation.dto.CursorRequest;
import liaison.linkit.search.presentation.dto.CursorResponse;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/search")
@Slf4j
public class TeamSearchController { // 팀 찾기 컨트롤러

    private final TeamSearchService teamSearchService;

    /**
     * 홈 화면용 주요 팀 목록 조회 API - 벤처 팀과 지원 프로젝트 팀을 조회합니다.
     *
     * @return 벤처 팀과 지원 프로젝트 팀 목록
     */
    @GetMapping("/featured")
    public CommonResponse<TeamListResponseDTO> getFeaturedTeams(@Auth final Accessor accessor) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        log.info("주요 팀 목록 조회 요청");

        TeamListResponseDTO featuredTeams = teamSearchService.getFeaturedTeams(optionalMemberId);

        return CommonResponse.onSuccess(featuredTeams);
    }

    /**
     * 팀 검색 API (커서 기반 페이지네이션) - 필터링 조건으로 팀을 검색합니다. - 기본 검색 시 벤처 팀과 지원 프로젝트 팀을 제외한 나머지 팀을 조회합니다.
     *
     * @param cursor 마지막으로 조회한 팀의 ID (선택적)
     * @param size 페이지 크기 (기본값: 20)
     * @param scaleName 팀 규모 (선택적)
     * @param cityName 활동 지역 (시/도) (선택적)
     * @param teamStateName 팀 현재 상태 (선택적)
     * @return 팀 목록과 커서 정보
     */
    @GetMapping
    public CommonResponse<CursorResponse<TeamResponseDTO.TeamInformMenu>> searchTeams(
            @Auth final Accessor accessor,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "scaleName", required = false) List<String> scaleName,
            @RequestParam(value = "cityName", required = false) List<String> cityName,
            @RequestParam(value = "teamStateName", required = false) List<String> teamStateName) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        CursorRequest cursorRequest = new CursorRequest(cursor, size);

        CursorResponse<TeamResponseDTO.TeamInformMenu> teamSearchResult =
                teamSearchService.searchTeamsWithCursor(
                        optionalMemberId, scaleName, cityName, teamStateName, cursorRequest);

        return CommonResponse.onSuccess(teamSearchResult);
    }
}
