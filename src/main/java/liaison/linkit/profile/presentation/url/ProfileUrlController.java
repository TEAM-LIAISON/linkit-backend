package liaison.linkit.profile.presentation.url;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO;
import liaison.linkit.profile.service.ProfileUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/url")
public class ProfileUrlController {

    public final ProfileUrlService profileUrlService;

    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileUrlResponseDTO.ProfileUrlItems> getProfileUrlDetail(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(profileUrlService.getProfileUrlDetail(accessor.getMemberId()));
    }

    // 자격증 단일 삭제 메서드
//    @DeleteMapping("/{profileUrlId}")
//    @MemberOnly
//    public CommonResponse<ProfileUrlResponseDTO.RemoveProfileUrl> deleteProfileUrl(
//            @PathVariable final Long profileUrlId
//    ) {
//        return CommonResponse.onSuccess(profileUrlService.deleteProfileUrl(profileUrlId));
//    }


}
