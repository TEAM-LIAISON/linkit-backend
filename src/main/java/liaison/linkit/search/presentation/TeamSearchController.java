package liaison.linkit.search.presentation;

import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * 팀원 검색 엔드포인트
     *
     * @param scaleName      팀 규모 (선택적)
     * @param isAnnouncement 모집 상태 (공고 존재 유무)
     * @param cityName       활동 지역 (시/도)
     * @param teamStateName  팀 현재 상태
     * @param page           페이지 번호 (기본값: 0)
     * @param size           페이지 크기 (기본값: 20)
     * @return 팀원 목록과 페이지 정보
     */
    @GetMapping
    public CommonResponse<Page<TeamInformMenu>> searchTeams(
        @Auth final Accessor accessor,
        @RequestParam(value = "scaleName", required = false) List<String> scaleName,
        @RequestParam(value = "isAnnouncement", required = false) Boolean isAnnouncement,
        @RequestParam(value = "cityName", required = false) List<String> cityName,
        @RequestParam(value = "teamStateName", required = false) List<String> teamStateName,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        if (accessor.isMember()) {
            Pageable pageable = PageRequest.of(page, 80, Sort.by("id").descending());
            Page<TeamInformMenu> teams = teamSearchService.searchTeamsInLoginState(accessor.getMemberId(), scaleName, isAnnouncement, cityName, teamStateName, pageable);
            return CommonResponse.onSuccess(teams);
        } else {
            Pageable pageable = PageRequest.of(page, 80, Sort.by("id").descending());
            Page<TeamInformMenu> teams = teamSearchService.searchTeamsInLogoutState(scaleName, isAnnouncement, cityName, teamStateName, pageable);
            return CommonResponse.onSuccess(teams);
        }
    }

}
