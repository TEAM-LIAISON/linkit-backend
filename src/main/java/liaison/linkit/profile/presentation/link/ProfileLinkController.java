package liaison.linkit.profile.presentation.link;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileLinkService;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/link")
@Slf4j
public class ProfileLinkController {

    public final ProfileLinkService profileLinkService;

    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Link", action = "GET_PROFILE_LINK_ITEMS", includeResult = true)
    public CommonResponse<ProfileLinkResponseDTO.ProfileLinkItems> getProfileLinkItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileLinkService.getProfileLinkItems(accessor.getMemberId()));
    }

    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_Link", action = "POST_UPDATE_PROFILE_LINK_ITEMS", includeResult = true)
    public CommonResponse<ProfileLinkResponseDTO.ProfileLinkItems> updateProfileLinkItems(
            @Auth final Accessor accessor,
            @RequestBody final ProfileLinkRequestDTO.AddProfileLinkRequest profileLinkRequest) {
        return CommonResponse.onSuccess(
                profileLinkService.updateProfileLinkItems(
                        accessor.getMemberId(), profileLinkRequest));
    }
}
