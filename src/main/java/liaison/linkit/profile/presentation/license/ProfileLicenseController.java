package liaison.linkit.profile.presentation.license;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileLicenseService;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.AddProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProfileLicenseController {

    public final ProfileLicenseService profileLicenseService;

    // 자격증 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_License", action = "GET_PROFILE_LICENSE_ITEMS", includeResult = true)
    public CommonResponse<ProfileLicenseItems> getProfileLicenseItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileLicenseService.getProfileLicenseItems(accessor.getMemberId()));
    }

    // 자격증 단일 조회
    @GetMapping("/{profileLicenseId}")
    @MemberOnly
    @Logging(item = "Profile_License", action = "GET_PROFILE_LICENSE_DETAIL", includeResult = true)
    public CommonResponse<ProfileLicenseResponseDTO.ProfileLicenseDetail> getProfileLicenseDetail(
            @Auth final Accessor accessor, @PathVariable final Long profileLicenseId) {
        return CommonResponse.onSuccess(
                profileLicenseService.getProfileLicenseDetail(
                        accessor.getMemberId(), profileLicenseId));
    }

    // 자격증 단일 생성
    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_License", action = "POST_ADD_PROFILE_LICENSE", includeResult = true)
    public CommonResponse<AddProfileLicenseResponse> addProfileLicense(
            @Auth final Accessor accessor,
            @RequestBody
                    final ProfileLicenseRequestDTO.AddProfileLicenseRequest
                            addProfileLicenseRequest) {
        return CommonResponse.onSuccess(
                profileLicenseService.addProfileLicense(
                        accessor.getMemberId(), addProfileLicenseRequest));
    }

    // 자격증 단일 수정
    @PostMapping("/{profileLicenseId}")
    @MemberOnly
    @Logging(item = "Profile_License", action = "POST_UPDATE_PROFILE_LICENSE", includeResult = true)
    public CommonResponse<ProfileLicenseResponseDTO.UpdateProfileLicenseResponse>
            updateProfileLicense(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileLicenseId,
                    @RequestBody
                            final ProfileLicenseRequestDTO.UpdateProfileLicenseRequest
                                    updateProfileLicenseRequest) {
        return CommonResponse.onSuccess(
                profileLicenseService.updateProfileLicense(
                        accessor.getMemberId(), profileLicenseId, updateProfileLicenseRequest));
    }

    // 자격증 단일 삭제
    @DeleteMapping("/{profileLicenseId}")
    @MemberOnly
    @Logging(item = "Profile_License", action = "POST_REMOVE_PROFILE_LICENSE", includeResult = true)
    public CommonResponse<ProfileLicenseResponseDTO.RemoveProfileLicenseResponse>
            removeProfileLicense(
                    @Auth final Accessor accessor, @PathVariable final Long profileLicenseId) {
        return CommonResponse.onSuccess(
                profileLicenseService.removeProfileLicense(
                        accessor.getMemberId(), profileLicenseId));
    }

    // 자격증 인증 단일 생성
    @PostMapping("/certification/{profileLicenseId}")
    @MemberOnly
    @Logging(
            item = "Profile_License",
            action = "POST_ADD_PROFILE_LICENSE_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse>
            addProfileLicenseCertification(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileLicenseId,
                    @RequestPart @Valid final MultipartFile profileLicenseCertificationFile) {
        return CommonResponse.onSuccess(
                profileLicenseService.addProfileLicenseCertification(
                        accessor.getMemberId(), profileLicenseId, profileLicenseCertificationFile));
    }

    // 자격증 인증 단일 삭제
    @DeleteMapping("/certification/{profileLicenseId}")
    @MemberOnly
    @Logging(
            item = "Profile_License",
            action = "DELETE_REMOVE_PROFILE_LICENSE_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse>
            removeProfileLicenseCertification(
                    @Auth final Accessor accessor, @PathVariable final Long profileLicenseId) {
        return CommonResponse.onSuccess(
                profileLicenseService.removeProfileLicenseCertification(
                        accessor.getMemberId(), profileLicenseId));
    }
}
