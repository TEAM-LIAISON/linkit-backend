package liaison.linkit.profile.presentation.education;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileEducationService;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItems;
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
@RequestMapping("/api/v1/profile/education")
@Slf4j
public class ProfileEducationController {

    private final ProfileEducationService profileEducationService;

    // 학력 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "GET_PROFILE_EDUCATION_ITEMS",
            includeResult = true)
    public CommonResponse<ProfileEducationItems> getProfileEducationItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileEducationService.getProfileEducationItems(accessor.getMemberId()));
    }

    // 학력 단일 조회
    @GetMapping("/{profileEducationId}")
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "GET_PROFILE_EDUCATION_DETAIL",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.ProfileEducationDetail>
            getProfileEducationDetail(
                    @Auth final Accessor accessor, @PathVariable final Long profileEducationId) {

        return CommonResponse.onSuccess(
                profileEducationService.getProfileEducationDetail(
                        accessor.getMemberId(), profileEducationId));
    }

    // 학력 단일 생성
    @PostMapping
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "POST_ADD_PROFILE_EDUCATION",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.AddProfileEducationResponse>
            addProfileEducation(
                    @Auth final Accessor accessor,
                    @RequestBody
                            final ProfileEducationRequestDTO.AddProfileEducationRequest
                                    addProfileEducationRequest) {
        return CommonResponse.onSuccess(
                profileEducationService.addProfileEducation(
                        accessor.getMemberId(), addProfileEducationRequest));
    }

    // 학력 단일 수정
    @PostMapping("/{profileEducationId}")
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "POST_UPDATE_PROFILE_EDUCATION",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.UpdateProfileEducationResponse>
            updateProfileEducation(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileEducationId,
                    @RequestBody
                            final ProfileEducationRequestDTO.UpdateProfileEducationRequest
                                    updateProfileEducationRequest) {

        return CommonResponse.onSuccess(
                profileEducationService.updateProfileEducation(
                        accessor.getMemberId(), profileEducationId, updateProfileEducationRequest));
    }

    // 학력 단일 삭제
    @DeleteMapping("/{profileEducationId}")
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "DELETE_REMOVE_PROFILE_EDUCATION",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.RemoveProfileEducationResponse>
            removeProfileEducation(
                    @Auth final Accessor accessor, @PathVariable final Long profileEducationId) {

        return CommonResponse.onSuccess(
                profileEducationService.removeProfileEducation(
                        accessor.getMemberId(), profileEducationId));
    }

    // 학력 인증 단일 생성
    @PostMapping("/certification/{profileEducationId}")
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "POST_ADD_PROFILE_EDUCATION_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.ProfileEducationCertificationResponse>
            addProfileEducationCertification(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileEducationId,
                    @RequestPart @Valid final MultipartFile profileEducationCertificationFile) {
        return CommonResponse.onSuccess(
                profileEducationService.addProfileEducationCertification(
                        accessor.getMemberId(),
                        profileEducationId,
                        profileEducationCertificationFile));
    }

    // 학력 인증 단일 삭제
    @DeleteMapping("/certification/{profileEducationId}")
    @MemberOnly
    @Logging(
            item = "Profile_Education",
            action = "DELETE_REMOVE_PROFILE_EDUCATION_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse>
            removeProfileEducationCertification(
                    @Auth final Accessor accessor, @PathVariable final Long profileEducationId) {
        return CommonResponse.onSuccess(
                profileEducationService.removeProfileEducationCertification(
                        accessor.getMemberId(), profileEducationId));
    }
}
