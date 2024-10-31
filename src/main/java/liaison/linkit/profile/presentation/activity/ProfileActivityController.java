package liaison.linkit.profile.presentation.activity;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.service.ProfileActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/activity")
@Slf4j
public class ProfileActivityController {

    private final ProfileActivityService profileActivityService;

    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityItems> getProfileActivityItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 활동 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileActivityService.getProfileActivityItems(accessor.getMemberId()));
    }

    @GetMapping("/{profileActivityId}")
    @MemberOnly
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityItem> getProfileActivityItem(
            @Auth final Accessor accessor,
            @PathVariable final Long profileActivityId
    ) {
        log.info("memberId = {}의 프로필 로그 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileActivityId);
        return CommonResponse.onSuccess(profileActivityService.getProfileActivityItem(accessor.getMemberId(), profileActivityId));
    }

    
}
