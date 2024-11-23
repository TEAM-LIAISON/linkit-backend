package liaison.linkit.profile.presentation.link;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import liaison.linkit.profile.service.ProfileLinkService;
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
    public CommonResponse<ProfileLinkResponseDTO.ProfileLinkItems> getProfileLinkItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 프로필 링크 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLinkService.getProfileLinkItems(accessor.getMemberId()));
    }

    @PostMapping
    @MemberOnly
    public CommonResponse<ProfileLinkResponseDTO.ProfileLinkItems> updateProfileLinkItems(
            @Auth final Accessor accessor,
            @RequestBody final ProfileLinkRequestDTO.AddProfileLinkRequest profileLinkRequest
    ) {
        log.info("memberId = {}의 프로필 링크 전체 수정 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profileLinkService.updateProfileLinkItems(accessor.getMemberId(), profileLinkRequest));
    }
}
