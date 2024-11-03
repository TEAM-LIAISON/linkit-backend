package liaison.linkit.profile.presentation.profile;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ProfileController {

    public final ProfileService profileService;

    @GetMapping("/profile/left/menu")
    @MemberOnly
    public CommonResponse<ProfileResponseDTO.ProfileLeftMenu> getProfileLeftMenu(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 왼쪽 메뉴 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileService.getProfileLeftMenu(accessor.getMemberId()));
    }
}
