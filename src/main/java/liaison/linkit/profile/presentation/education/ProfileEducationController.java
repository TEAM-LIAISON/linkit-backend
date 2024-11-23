package liaison.linkit.profile.presentation.education;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItems;
import liaison.linkit.profile.service.ProfileEducationService;
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
    public CommonResponse<ProfileEducationItems> getProfileEducationItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 학력 전체 조회 요청이 컨트롤러 계층에 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileEducationService.getProfileEducationItems(accessor.getMemberId()));
    }

    // 학력 단일 조회
    @GetMapping("/{profileEducationId}")
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.ProfileEducationDetail> getProfileEducationDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long profileEducationId
    ) {
        log.info("memberId = {}의 profileEducationId = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileEducationId);
        return CommonResponse.onSuccess(profileEducationService.getProfileEducationDetail(accessor.getMemberId(), profileEducationId));
    }

    // 학력 단일 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.AddProfileEducationResponse> addProfileEducation(
            @Auth final Accessor accessor,
            @RequestBody final ProfileEducationRequestDTO.AddProfileEducationRequest addProfileEducationRequest
    ) {
        log.info("memberId = {}의 프로필 학력 추가 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileEducationService.addProfileEducation(accessor.getMemberId(), addProfileEducationRequest));
    }

    // 학력 단일 수정
    @PostMapping("/{profileEducationId}")
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.UpdateProfileEducationResponse> updateProfileEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long profileEducationId,
            @RequestBody final ProfileEducationRequestDTO.UpdateProfileEducationRequest updateProfileEducationRequest
    ) {
        log.info("memberId = {}의 profileEducationId = {}에 대한 프로필 학력 수정 요청이 발생했습니다.", accessor.getMemberId(), profileEducationId);
        return CommonResponse.onSuccess(profileEducationService.updateProfileEducation(accessor.getMemberId(), profileEducationId, updateProfileEducationRequest));
    }

    // 학력 단일 삭제
    @DeleteMapping("/{profileEducationId}")
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.RemoveProfileEducationResponse> removeProfileEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long profileEducationId
    ) {
        log.info("memberId = {}의 profileEducationId = {}에 대한 학력 단일 삭제 요청이 컨트롤러 계층에 발생했습니다.", accessor.getMemberId(), profileEducationId);
        return CommonResponse.onSuccess(profileEducationService.removeProfileEducation(accessor.getMemberId(), profileEducationId));
    }

    // 학력 인증 단일 생성
    @PostMapping("/certification/{profileEducationId}")
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.ProfileEducationCertificationResponse> addProfileEducationCertification(
            @Auth final Accessor accessor,
            @PathVariable final Long profileEducationId,
            @RequestPart @Valid final MultipartFile profileEducationCertificationFile
    ) {
        log.info("memberId = {}의 profileEducationId = {}에 대한 학력 인증서 단일 생성 요청이 컨트롤러 계층에 발생했습니다.", accessor.getMemberId(), profileEducationId);
        return CommonResponse.onSuccess(profileEducationService.addProfileEducationCertification(accessor.getMemberId(), profileEducationId, profileEducationCertificationFile));
    }

    // 학력 인증 단일 삭제
    @DeleteMapping("/certification/{profileEducationId}")
    @MemberOnly
    public CommonResponse<ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse> removeProfileEducationCertification(
            @Auth final Accessor accessor,
            @PathVariable final Long profileEducationId
    ) {
        log.info("memberId = {}의 profileEducationId = {}에 대한 학력 인증서 단일 삭제 요청이 컨트롤러 계층에 발생했습니다.", accessor.getMemberId(), profileEducationId);
        return CommonResponse.onSuccess(profileEducationService.removeProfileEducationCertification(accessor.getMemberId(), profileEducationId));
    }
}
