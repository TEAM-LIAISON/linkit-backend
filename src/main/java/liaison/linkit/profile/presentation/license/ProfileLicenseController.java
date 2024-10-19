package liaison.linkit.profile.presentation.license;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import liaison.linkit.profile.service.ProfileLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    // 자격증 단일 수정 메서드
    @PostMapping
    @MemberOnly
    public CommonResponse<ProfileLicenseRequestDTO.EditProfileLicense> editProfileLicense(
            @Auth final Accessor accessor,
            @RequestBody ProfileLicenseRequestDTO.EditProfileLicense request,
            @RequestPart(required = false) MultipartFile profileLicenseCertificationFile
    ) {
        return CommonResponse.onSuccess(profileLicenseService.editProfileLicense(accessor.getMemberId(), request, profileLicenseCertificationFile));
    }

    // 자격증 단일 삭제 메서드
    @DeleteMapping("/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.RemoveProfileLicense> deleteProfileLicense(
            @PathVariable final Long profileLicenseId
    ) {
        return CommonResponse.onSuccess(profileLicenseService.deleteProfileLicense(profileLicenseId));
    }
}
