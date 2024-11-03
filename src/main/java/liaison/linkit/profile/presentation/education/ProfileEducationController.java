package liaison.linkit.profile.presentation.education;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.service.ProfileEducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/education")
@Slf4j
public class ProfileEducationController {

    private final ProfileEducationService profileEducationService;

    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.ProfileEducationItems> getProfileEducationItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 학력 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileEducationService.getProfileEducationItems(accessor.getMemberId()));
    }
    
}
