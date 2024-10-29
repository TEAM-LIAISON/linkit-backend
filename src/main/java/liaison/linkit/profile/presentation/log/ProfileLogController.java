package liaison.linkit.profile.presentation.log;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.service.ProfileLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/log")
@Slf4j
public class ProfileLogController {

    private final ProfileLogService profileLogService;

    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItems> getProfileLogItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 로그 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLogService.getProfileLogItems(accessor.getMemberId()));
    }

    @GetMapping("/{profileLogId}")
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItem> getProfileLogItem(
            @Auth final Accessor accessor,
            @PathVariable final Long profileLogId
    ) {
        log.info("memberId = {}의 프로필 로그 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileLogId);
        return CommonResponse.onSuccess(profileLogService.getProfileLogItem(accessor.getMemberId(), profileLogId));
    }
}
