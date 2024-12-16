package liaison.linkit.scrap.presentation;


import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.scrap.business.ProfileScrapService;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapRequestDTO;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/scrap")
public class ProfileScrapController {

    private final ProfileScrapService profileScrapService;

    @PostMapping("/{emailId}")
    @MemberOnly
    public CommonResponse<ProfileScrapResponseDTO.UpdateProfileScrap> updateProfileScrap(
            @Auth final Accessor accessor,
            @PathVariable final String emailId,
            @RequestBody final ProfileScrapRequestDTO.UpdateProfileScrapRequest updateProfileScrapRequest  // 변경하고자 하는 boolean 상태
    ) {
        return CommonResponse.onSuccess(profileScrapService.updateProfileScrap(accessor.getMemberId(), emailId, updateProfileScrapRequest));
    }
}
