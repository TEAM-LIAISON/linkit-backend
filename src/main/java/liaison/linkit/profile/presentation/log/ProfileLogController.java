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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return CommonResponse.onSuccess(profileLogService.getProfileLogItems(accessor.getMemberId()));
    }

    @GetMapping("/profileLogId")
    @MemberOnly
    public CommonResponse<ProfileLogResponseDTO.ProfileLogItem> getProfileLogItem(
            @Auth final Accessor accessor,
            @RequestParam final Long profileLogId
    ) {
        return CommonResponse.onSuccess(profileLogService.getProfileLogItem(accessor.getMemberId(), profileLogId));
    }
}
