package liaison.linkit.profile.presentation.activity;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.service.ProfileActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/activity")
@Slf4j
public class ProfileActivityController {

    private final ProfileActivityService profileActivityService;

    // 이력 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityItems> getProfileActivityItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 활동 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileActivityService.getProfileActivityItems(accessor.getMemberId()));
    }

    // 이력 상세 조회
    @GetMapping("/{profileActivityId}")
    @MemberOnly
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityDetail> getProfileActivityDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long profileActivityId
    ) {
        log.info("memberId = {}의 프로필 로그 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profileActivityId);
        return CommonResponse.onSuccess(profileActivityService.getProfileActivityDetail(accessor.getMemberId(), profileActivityId));
    }

    @PostMapping
    @MemberOnly
    public CommonResponse<ProfileActivityResponseDTO.ProfileActivityResponse> addProfileActivity(
            @Auth final Accessor accessor,
            @RequestBody final ProfileActivityRequestDTO.AddProfileActivityRequest addProfileActivityRequest
    ) {
        log.info("memberId = {}의 프로필 이력 추가 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileActivityService.addProfileActivity(accessor.getMemberId(), addProfileActivityRequest));
    }
}
