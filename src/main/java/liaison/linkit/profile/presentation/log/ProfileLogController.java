package liaison.linkit.profile.presentation.log;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogTypeResponse;
import liaison.linkit.profile.service.ProfileLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/log")
@Slf4j
public class ProfileLogController {

    private final ProfileLogService profileLogService;

    // 로그 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItems> getProfileLogItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 로그 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLogService.getProfileLogItems(accessor.getMemberId()));
    }

    // 로그 상세 조회
    @GetMapping("/{profileLogId}")
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItem> getProfileLogItem(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLogId
    ) {
        log.info("memberId = {}의 프로필 로그 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileLogId);
        return CommonResponse.onSuccess(profileLogService.getProfileLogItem(accessor.getMemberId(), profileLogId));
    }

    // 로그 추가
    @PostMapping
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.AddProfileLogResponse> addProfileLog(
            @Auth final Accessor accessor,
            @RequestBody ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest
    ) {
        log.info("memberId = {}의 프로필 로그에 대한 생성 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLogService.addProfileLog(accessor.getMemberId(), addProfileLogRequest));
    }

    // 로그 삭제
    @DeleteMapping("/{profileLogId}")
    @MemberOnly
    public CommonResponse<RemoveProfileLogResponse> deleteProfileLog(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLogId
    ) {
        log.info("memberId = {}의 프로필 로그 = {}에 대한 삭제 요청이 발생했습니다.", accessor.getMemberId(), profileLogId);
        return CommonResponse.onSuccess(profileLogService.removeProfileLog(accessor.getMemberId(), profileLogId));
    }

    // 로그 대표글/일반글 변경
    @PostMapping("/{profileLogId}")
    @MemberOnly
    public CommonResponse<UpdateProfileLogTypeResponse> updateProfileLogPublicState(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLogId,
            @RequestBody final ProfileLogRequestDTO.UpdateProfileLogType updateProfileLogType
    ) {
        log.info("memberId = {}의 프로필 로그 = {}에 대한 대표글 설정 수정 요청이 발생했습니다.", accessor.getMemberId(), profileLogId);
        return CommonResponse.onSuccess(profileLogService.updateProfileLogType(accessor.getMemberId(), profileLogId, updateProfileLogType));
    }
}
