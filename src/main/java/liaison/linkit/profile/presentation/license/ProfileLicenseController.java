package liaison.linkit.profile.presentation.license;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.AddProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import liaison.linkit.profile.business.service.ProfileLicenseService;
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
    public CommonResponse<ProfileLicenseItems> getProfileLicenseItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 자격증 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLicenseService.getProfileLicenseItems(accessor.getMemberId()));
    }

    // 자격증 단일 조회
    @GetMapping("/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.ProfileLicenseDetail> getProfileLicenseDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLicenseId
    ) {
        log.info("memberId = {}의 프로필 자격증 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileLicenseId);
        return CommonResponse.onSuccess(profileLicenseService.getProfileLicenseDetail(accessor.getMemberId(), profileLicenseId));
    }

    // 자격증 단일 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<AddProfileLicenseResponse> addProfileLicense(
            @Auth final Accessor accessor,
            @RequestBody final ProfileLicenseRequestDTO.AddProfileLicenseRequest addProfileLicenseRequest
    ) {
        log.info("memberId = {}의 프로필 자격증 추가 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLicenseService.addProfileLicense(accessor.getMemberId(), addProfileLicenseRequest));
    }

    // 자격증 단일 수정
    @PostMapping("/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.UpdateProfileLicenseResponse> updateProfileLicense(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLicenseId,
            @RequestBody final ProfileLicenseRequestDTO.UpdateProfileLicenseRequest updateProfileLicenseRequest
    ) {
        log.info("memberId = {}의 프로필 자격증 수정 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLicenseService.updateProfileLicense(accessor.getMemberId(), profileLicenseId, updateProfileLicenseRequest));
    }

    // 자격증 단일 삭제
    @DeleteMapping("/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.RemoveProfileLicenseResponse> removeProfileLicense(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLicenseId
    ) {
        return CommonResponse.onSuccess(profileLicenseService.removeProfileLicense(accessor.getMemberId(), profileLicenseId));
    }

    // 자격증 인증 단일 생성
    @PostMapping("/certification/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse> addProfileLicenseCertification(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLicenseId,
            @RequestPart @Valid final MultipartFile profileLicenseCertificationFile
    ) {
        return CommonResponse.onSuccess(profileLicenseService.addProfileLicenseCertification(accessor.getMemberId(), profileLicenseId, profileLicenseCertificationFile));
    }

    // 자격증 인증 단일 삭제
    @DeleteMapping("/certification/{profileLicenseId}")
    @MemberOnly
    public CommonResponse<ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse> removeProfileLicenseCertification(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLicenseId
    ) {
        return CommonResponse.onSuccess(profileLicenseService.removeProfileLicenseCertification(accessor.getMemberId(), profileLicenseId));
    }
}
