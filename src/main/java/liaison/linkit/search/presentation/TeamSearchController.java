package liaison.linkit.search.presentation;

import java.util.Optional;

import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.model.TeamSearchCondition;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/search")
@Slf4j
public class TeamSearchController { // 팀 찾기 컨트롤러

    private final TeamSearchService teamSearchService;

    @GetMapping("/featured")
    public CommonResponse<TeamListResponseDTO> getFeaturedTeams(
            @CurrentMemberId Optional<Long> memberId) {
        return CommonResponse.onSuccess(teamSearchService.getFeaturedTeams(memberId));
    }

    /** 커서 기반 페이지네이션과 필터를 이용해 팀 검색을 수행합니다. */
    @GetMapping
    public CommonResponse<CursorResponse<TeamInformMenu>> searchTeams(
            @CurrentMemberId Optional<Long> memberId,
            CursorRequest cursorRequest,
            TeamSearchCondition condition) {
        return CommonResponse.onSuccess(
                teamSearchService.searchTeamsWithCursor(memberId, condition, cursorRequest));
    }
}
