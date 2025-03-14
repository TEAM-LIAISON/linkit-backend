package liaison.linkit.visit.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.visit.business.service.VisitService;
import liaison.linkit.visit.presentation.dto.VisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class VisitController {

    private final VisitService visitService;

    // 프로필 방문자를 조회한다.
    @GetMapping("/profile/visit")
    @MemberOnly
    @Logging(item = "Profile_Visit", action = "GET_PROFILE_VISIT_INFORMS", includeResult = true)
    public CommonResponse<VisitResponseDTO.VisitInforms> getProfileVisitInforms(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                visitService.getProfileVisitInforms(accessor.getMemberId()));
    }

    // 프로필 방문자를 조회한다.
    @GetMapping("/team/{teamCode}/visit")
    @MemberOnly
    @Logging(item = "Team_Visit", action = "GET_TEAM_VISIT_INFORMS", includeResult = true)
    public CommonResponse<VisitResponseDTO.VisitInforms> getTeamVisitInforms(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                visitService.getTeamVisitInforms(accessor.getMemberId(), teamCode));
    }
}
