package liaison.linkit.search.presentation;

import java.util.List;
import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.search.presentation.dto.CursorRequest;
import liaison.linkit.search.presentation.dto.TeamSearchResponseDTO;
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
     * 팀원 검색 엔드포인트 (커서 기반 페이지네이션)
     *
     * @param cursor        마지막으로 조회한 팀의 ID (선택적)
     * @param size          페이지 크기 (기본값: 20)
     * @param scaleName     팀 규모 (선택적)
     * @param cityName      활동 지역 (시/도) (선택적)
     * @param teamStateName 팀 현재 상태 (선택적)
     * @return 팀원 목록과 커서 정보
     */
    @GetMapping
    public CommonResponse<TeamSearchResponseDTO> searchTeams(
            @Auth final Accessor accessor,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "scaleName", required = false) List<String> scaleName,
            @RequestParam(value = "cityName", required = false) List<String> cityName,
            @RequestParam(value = "teamStateName", required = false) List<String> teamStateName) {

        // 로그인 여부에 따라 Optional 생성
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        // 커서 요청 객체 생성
        CursorRequest cursorRequest = new CursorRequest(cursor, size);

        TeamSearchResponseDTO teamSearchResponseDTO =
                teamSearchService.searchTeams(
                        optionalMemberId, scaleName, cityName, teamStateName, cursorRequest);

        return CommonResponse.onSuccess(teamSearchResponseDTO);
    }
}
