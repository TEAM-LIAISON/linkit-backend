package liaison.linkit.profile.presentation.miniProfile;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/miniProfile")
public class MiniProfileController {

    public final MiniProfileService miniProfileService;

    // 미니 프로필을 조회한다
    @GetMapping
    @MemberOnly
    public CommonResponse<MiniProfileResponseDTO.MiniProfileDetail> getMiniProfileDetail(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(miniProfileService.getMiniProfileDetail(accessor.getMemberId()));
    }

    @PostMapping
    @MemberOnly
    public CommonResponse<MiniProfileResponseDTO.SaveMiniProfile> saveMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart(required = false) MultipartFile profileImage,
            @RequestPart @Valid MiniProfileRequestDTO.SaveMiniProfileRequest saveMiniProfileRequest
    ) {
        return CommonResponse.onSuccess(miniProfileService.saveMiniProfile(accessor.getMemberId(), profileImage, saveMiniProfileRequest));
    }
}
