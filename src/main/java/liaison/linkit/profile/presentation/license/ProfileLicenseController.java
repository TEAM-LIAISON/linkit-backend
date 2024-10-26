package liaison.linkit.profile.presentation.license;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import liaison.linkit.profile.service.ProfileLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/license")
public class ProfileLicenseController {

    public final ProfileLicenseService profileLicenseService;

    // 자격증 리스트 조회 메서드
    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileLicenseItems> getProfileLicenseItems(@Auth final Accessor accessor) {
        return CommonResponse.onSuccess(profileLicenseService.getProfileLicenseItems(accessor.getMemberId()));
    }
}
