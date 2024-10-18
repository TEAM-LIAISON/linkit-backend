package liaison.linkit.profile.presentation.miniProfile;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/miniProfile")
public class MiniProfileController {

    public final MiniProfileService miniProfileService;

    @GetMapping
    @MemberOnly
    public CommonResponse<MiniProfileResponseDTO.MiniProfileDetail> getMiniProfileDetail(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(miniProfileService.getMiniProfileDetail(accessor.getMemberId()));
    }


}
