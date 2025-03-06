package liaison.linkit.profile.presentation.miniProfile;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.MiniProfileService;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class MiniProfileController {

    public final MiniProfileService miniProfileService;

    // 미니 프로필 조회
    @GetMapping("/miniProfile")
    @MemberOnly
    @Logging(item = "MiniProfile", action = "GET_MINI_PROFILE_DETAIL", includeResult = true)
    public CommonResponse<MiniProfileDetailResponse> getMiniProfileDetail(
            @Auth final Accessor accessor) {
        log.info("memberId = {}의 미니프로필 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(
                miniProfileService.getMiniProfileDetail(accessor.getMemberId()));
    }

    // 미니 프로필 업데이트
    @PostMapping("/miniProfile")
    @MemberOnly
    @Logging(item = "MiniProfile", action = "POST_UPDATE_MINI_PROFILE", includeResult = true)
    public CommonResponse<MiniProfileResponseDTO.UpdateMiniProfileResponse> updateMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart(required = false) MultipartFile profileImage,
            @RequestPart @Valid
                    MiniProfileRequestDTO.UpdateMiniProfileRequest updateMiniProfileRequest) {
        log.info("memberId = {}의 미니프로필 수정 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(
                miniProfileService.updateMiniProfile(
                        accessor.getMemberId(), profileImage, updateMiniProfileRequest));
    }
}
