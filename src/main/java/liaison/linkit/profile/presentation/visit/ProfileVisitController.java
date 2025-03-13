package liaison.linkit.profile.presentation.visit;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileVisitService;
import liaison.linkit.profile.presentation.visit.dto.ProfileVisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/profile/{emailId}/visit")
@Slf4j
public class ProfileVisitController {

    private final ProfileVisitService profileVisitService;

    // 프로필 방문자를 조회한다.
    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Visit", action = "GET_PROFILE_VISIT_INFORMS", includeResult = true)
    public CommonResponse<ProfileVisitResponseDTO.ProfileVisitInformation> getProfileVisitInforms(
            @Auth final Accessor accessor, @PathVariable final String emailId) {
        return CommonResponse.onSuccess(
                profileVisitService.getProfileVisitInforms(accessor.getMemberId(), emailId));
    }
}
