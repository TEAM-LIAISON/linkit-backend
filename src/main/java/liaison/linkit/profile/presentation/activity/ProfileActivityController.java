package liaison.linkit.profile.presentation.activity;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileActivityService;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.AddProfileActivityResponse;
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
@RequestMapping("/api/v1/profile/activity")
@Slf4j
public class ProfileActivityController {

    private final ProfileActivityService profileActivityService;

    // 이력 전체 조회 (명세 완료)
    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Activity", action = "GET_PROFILE_ACTIVITY_ITEMS", includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityItems> getProfileActivityItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileActivityService.getProfileActivityItems(accessor.getMemberId()));
    }

    // 이력 단일 조회 (명세 완료)
    @GetMapping("/{profileActivityId}")
    @MemberOnly
    @Logging(
            item = "Profile_Activity",
            action = "GET_PROFILE_ACTIVITY_DETAIL",
            includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityDetail>
            getProfileActivityDetail(
                    @Auth final Accessor accessor, @PathVariable final Long profileActivityId) {
        return CommonResponse.onSuccess(
                profileActivityService.getProfileActivityDetail(
                        accessor.getMemberId(), profileActivityId));
    }

    // 이력 단일 생성 (명세 완료)
    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_Activity", action = "POST_ADD_PROFILE_ACTIVITY", includeResult = true)
    public CommonResponse<AddProfileActivityResponse> addProfileActivity(
            @Auth final Accessor accessor,
            @RequestBody
                    final ProfileActivityRequestDTO.AddProfileActivityRequest
                            addProfileActivityRequest) {
        return CommonResponse.onSuccess(
                profileActivityService.addProfileActivity(
                        accessor.getMemberId(), addProfileActivityRequest));
    }

    // 이력 단일 수정 (명세 완료)
    @PostMapping("/{profileActivityId}")
    @MemberOnly
    @Logging(
            item = "Profile_Activity",
            action = "POST_UPDATE_PROFILE_ACTIVITY",
            includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.UpdateProfileActivityResponse>
            updateProfileActivity(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileActivityId,
                    @RequestBody
                            final ProfileActivityRequestDTO.UpdateProfileActivityRequest
                                    updateProfileActivityRequest) {
        return CommonResponse.onSuccess(
                profileActivityService.updateProfileActivity(
                        accessor.getMemberId(), profileActivityId, updateProfileActivityRequest));
    }

    // 이력 단일 삭제 (명세 완료)
    @DeleteMapping("/{profileActivityId}")
    @MemberOnly
    @Logging(
            item = "Profile_Activity",
            action = "DELETE_REMOVE_PROFILE_ACTIVITY",
            includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.RemoveProfileActivityResponse>
            removeProfileActivity(
                    @Auth final Accessor accessor, @PathVariable final Long profileActivityId) {
        return CommonResponse.onSuccess(
                profileActivityService.removeProfileActivity(
                        accessor.getMemberId(), profileActivityId));
    }

    // 이력 인증 단일 생성 (명세 완료)
    @PostMapping("/certification/{profileActivityId}")
    @MemberOnly
    @Logging(
            item = "Profile_Activity",
            action = "POST_ADD_PROFILE_ACTIVITY_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityCertificationResponse>
            addProfileActivityCertification(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileActivityId,
                    @RequestPart @Valid final MultipartFile profileActivityCertificationFile) {
        return CommonResponse.onSuccess(
                profileActivityService.addProfileActivityCertification(
                        accessor.getMemberId(),
                        profileActivityId,
                        profileActivityCertificationFile));
    }

    // 이력 인증 단일 삭제 (명세 완료)
    @DeleteMapping("/certification/{profileActivityId}")
    @MemberOnly
    @Logging(
            item = "Profile_Activity",
            action = "DELETE_REMOVE_PROFILE_ACTIVITY_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse>
            removeProfileActivityCertification(
                    @Auth final Accessor accessor, @PathVariable final Long profileActivityId) {
        return CommonResponse.onSuccess(
                profileActivityService.removeProfileActivityCertification(
                        accessor.getMemberId(), profileActivityId));
    }
}
