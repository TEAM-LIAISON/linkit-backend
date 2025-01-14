package liaison.linkit.profile.presentation.profile;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.business.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ProfileController {

    public final ProfileService profileService;

    // 내 기본 정보에서 프로필 왼쪽 메뉴
    @GetMapping("/profile/left/menu")
    @MemberOnly
    public CommonResponse<ProfileResponseDTO.ProfileLeftMenu> getProfileLeftMenu(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 왼쪽 메뉴 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileService.getProfileLeftMenu(accessor.getMemberId()));
    }

    // 내 프로필 조회
    @GetMapping("/profile/{emailId}")
    public CommonResponse<ProfileResponseDTO.ProfileDetail> getProfileDetail(
            @PathVariable final String emailId,
            @Auth final Accessor accessor
    ) {
        if (accessor.isMember()) {
            Long memberId = accessor.getMemberId();
            log.info("memberId = {}의 프로필 상세 조회 요청이 발생했습니다.", memberId);
            return CommonResponse.onSuccess(profileService.getLoggedInProfileDetail(memberId, emailId));
        } else {
            log.info("emailId = {}에 대한 프로필 상세 조회 요청이 발생했습니다.", emailId);
            return CommonResponse.onSuccess(profileService.getLoggedOutProfileDetail(emailId));
        }
    }

    // 홈화면에서 팀원 조회 (최대 6개)
    @GetMapping("/home/profile")
    public CommonResponse<ProfileResponseDTO.ProfileInformMenus> getHomeProfileInformMenus() {
        return CommonResponse.onSuccess(profileService.getHomeProfileInformMenus());
    }
}
