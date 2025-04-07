package liaison.linkit.profile.presentation.profile;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileService;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
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
    @Logging(item = "Profile", action = "GET_PROFILE_LEFT_MENUS", includeResult = true)
    public CommonResponse<ProfileResponseDTO.ProfileLeftMenu> getProfileLeftMenu(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(profileService.getProfileLeftMenu(accessor.getMemberId()));
    }

    // 내 프로필 조회
    @GetMapping("/profile/{emailId}")
    @Logging(item = "Profile", action = "GET_PROFILE_DETAIL", includeResult = true)
    public CommonResponse<ProfileResponseDTO.ProfileDetail> getProfileDetail(
            @PathVariable final String emailId, @Auth final Accessor accessor) {
        if (accessor.isMember()) {
            Optional<Long> optionalMemberId =
                    accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

            return CommonResponse.onSuccess(
                    profileService.getLoggedInProfileDetail(optionalMemberId, emailId));
        } else {
            return CommonResponse.onSuccess(profileService.getLoggedOutProfileDetail(emailId));
        }
    }

    @GetMapping("/home/profile")
    @Logging(item = "Profile", action = "GET_HOME_PROFILE_INFORM_MENUS", includeResult = false)
    public CommonResponse<ProfileResponseDTO.ProfileInformMenus> getHomeProfileInformMenus(
            @CurrentMemberId Optional<Long> memberId) {
        return CommonResponse.onSuccess(profileService.getHomeProfileInformMenus(memberId));
    }

    @GetMapping("/profile/summary/inform/{emailId}")
    @Logging(item = "Profile", action = "GET_PROFILE_SUMMARY_INFORM", includeResult = true)
    public CommonResponse<ProfileResponseDTO.ProfileSummaryInform> getProfileSummaryInform(
            @PathVariable final String emailId) {
        return CommonResponse.onSuccess(profileService.getProfileSummaryInform(emailId));
    }
}
