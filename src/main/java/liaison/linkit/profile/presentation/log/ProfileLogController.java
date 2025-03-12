package liaison.linkit.profile.presentation.log;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileLogService;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogTypeResponse;
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
@RequestMapping("/api/v1/profile/log")
@Slf4j
public class ProfileLogController {

    private final ProfileLogService profileLogService;

    // 로그 첨부 이미지 저장
    @PostMapping("/body/image")
    @MemberOnly
    @Logging(item = "Profile_Log", action = "POST_ADD_PROFILE_LOG_BODY_IMAGE", includeResult = true)
    public CommonResponse<AddProfileLogBodyImageResponse> addProfileLogBodyImage(
            @Auth final Accessor accessor,
            @RequestPart @Valid final MultipartFile profileLogBodyImage) {
        return CommonResponse.onSuccess(
                profileLogService.addProfileLogBodyImage(
                        accessor.getMemberId(), profileLogBodyImage));
    }

    // 로그 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Log", action = "GET_PROFILE_LOG_ITEMS", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItems> getProfileLogItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileLogService.getProfileLogItems(accessor.getMemberId()));
    }

    // 로그 뷰어 전체 조회
    @GetMapping("/view/{emailId}")
    @Logging(item = "Profile_Log", action = "GET_PROFILE_LOG_VIEW_ITEMS", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItems> getProfileLogViewItems(
            @PathVariable final String emailId) {
        return CommonResponse.onSuccess(profileLogService.getProfileLogViewItems(emailId));
    }

    // 로그 뷰어 단일 조회
    @GetMapping("/view/detail/{profileLogId}")
    @Logging(item = "Profile_Log", action = "GET_PROFILE_LOG_VIEW_ITEM", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItem> getProfileLogViewItem(
            @PathVariable final Long profileLogId) {
        return CommonResponse.onSuccess(profileLogService.getProfileLogViewItem(profileLogId));
    }

    // 로그 상세 조회
    @GetMapping("/{profileLogId}")
    @MemberOnly
    @Logging(item = "Profile_Log", action = "GET_PROFILE_LOG_ITEM", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItem> getProfileLogItem(
            @Auth final Accessor accessor, @PathVariable final Long profileLogId) {

        return CommonResponse.onSuccess(
                profileLogService.getProfileLogItem(accessor.getMemberId(), profileLogId));
    }

    // 로그 추가
    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_Log", action = "POST_ADD_PROFILE_LOG", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.AddProfileLogResponse> addProfileLog(
            @Auth final Accessor accessor,
            @RequestBody ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest) {
        return CommonResponse.onSuccess(
                profileLogService.addProfileLog(accessor.getMemberId(), addProfileLogRequest));
    }

    // 로그 수정
    @PostMapping("/{profileLogId}")
    @MemberOnly
    @Logging(item = "Profile_Log", action = "POST_UPDATE_PROFILE_LOG", includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.UpdateProfileLogResponse> updateProfileLog(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLogId,
            @RequestBody ProfileLogRequestDTO.UpdateProfileLogRequest updateProfileLogRequest) {
        return CommonResponse.onSuccess(
                profileLogService.updateProfileLog(
                        accessor.getMemberId(), profileLogId, updateProfileLogRequest));
    }

    // 로그 삭제
    @DeleteMapping("/{profileLogId}")
    @MemberOnly
    @Logging(item = "Profile_Log", action = "DELETE_PROFILE_LOG", includeResult = true)
    public CommonResponse<RemoveProfileLogResponse> deleteProfileLog(
            @Auth final Accessor accessor, @PathVariable final Long profileLogId) {
        return CommonResponse.onSuccess(
                profileLogService.removeProfileLog(accessor.getMemberId(), profileLogId));
    }

    // 로그 대표글로 수정
    @PostMapping("/type/{profileLogId}")
    @MemberOnly
    @Logging(item = "Profile_Log", action = "POST_UPDATE_PROFILE_LOG_TYPE", includeResult = true)
    public CommonResponse<UpdateProfileLogTypeResponse> updateProfileLogType(
            @Auth final Accessor accessor, @PathVariable final Long profileLogId) {
        return CommonResponse.onSuccess(
                profileLogService.updateProfileLogType(accessor.getMemberId(), profileLogId));
    }

    // 로그 공개 여부 수정
    @PostMapping("/state/{profileLogId}")
    @MemberOnly
    @Logging(
            item = "Profile_Log",
            action = "POST_UPDATE_PROFILE_LOG_PUBLIC_STATE",
            includeResult = true)
    public CommonResponse<ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse>
            updateProfileLogPublicState(
                    @Auth final Accessor accessor, @PathVariable final Long profileLogId) {
        return CommonResponse.onSuccess(
                profileLogService.updateProfileLogPublicState(
                        accessor.getMemberId(), profileLogId));
    }
}
