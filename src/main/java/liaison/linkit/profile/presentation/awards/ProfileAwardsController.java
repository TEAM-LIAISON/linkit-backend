package liaison.linkit.profile.presentation.awards;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileAwardsService;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/awards")
@Slf4j
public class ProfileAwardsController {

    private final ProfileAwardsService profileAwardsService;

    // 수상 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Awards", action = "GET_PROFILE_AWARDS_ITEMS", includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.ProfileAwardsItems> getProfileAwardsItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileAwardsService.getProfileAwardsItems(accessor.getMemberId()));
    }

    // 수상 단일 조회
    @GetMapping("/{profileAwardsId}")
    @MemberOnly
    @Logging(item = "Profile_Awards", action = "GET_PROFILE_AWARDS_DETAIL", includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.ProfileAwardsDetail> getProfileAwardsDetail(
            @Auth final Accessor accessor, @PathVariable final Long profileAwardsId) {
        return CommonResponse.onSuccess(
                profileAwardsService.getProfileAwardsDetail(
                        accessor.getMemberId(), profileAwardsId));
    }

    // 수상 단일 생성
    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_Awards", action = "POST_ADD_PROFILE_AWARDS", includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.AddProfileAwardsResponse> addProfileAwards(
            @Auth final Accessor accessor,
            @RequestBody
                    final ProfileAwardsRequestDTO.AddProfileAwardsRequest addProfileAwardsRequest) {
        return CommonResponse.onSuccess(
                profileAwardsService.addProfileAwards(
                        accessor.getMemberId(), addProfileAwardsRequest));
    }

    // 수상 단일 수정
    @PostMapping("/{profileAwardsId}")
    @MemberOnly
    @Logging(item = "Profile_Awards", action = "POST_UPDATE_PROFILE_AWARDS", includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.UpdateProfileAwardsResponse> updateProfileAwards(
            @Auth final Accessor accessor,
            @PathVariable final Long profileAwardsId,
            @RequestBody
                    final ProfileAwardsRequestDTO.UpdateProfileAwardsRequest
                            updateProfileAwardsRequest) {
        return CommonResponse.onSuccess(
                profileAwardsService.updateProfileAwards(
                        accessor.getMemberId(), profileAwardsId, updateProfileAwardsRequest));
    }

    // 수상 단일 삭제
    @DeleteMapping("/{profileAwardsId}")
    @MemberOnly
    @Logging(item = "Profile_Awards", action = "DELETE_REMOVE_PROFILE_AWARDS", includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.RemoveProfileAwardsResponse> removeProfileAwards(
            @Auth final Accessor accessor, @PathVariable final Long profileAwardsId) {
        return CommonResponse.onSuccess(
                profileAwardsService.removeProfileAwards(accessor.getMemberId(), profileAwardsId));
    }

    // 수상 인증 단일 생성
    @PostMapping("/certification/{profileAwardsId}")
    @MemberOnly
    @Logging(
            item = "Profile_Awards",
            action = "POST_ADD_PROFILE_AWARDS_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.ProfileAwardsCertificationResponse>
            addProfileAwardsCertification(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileAwardsId,
                    @RequestPart @Valid final MultipartFile profileAwardsCertificationFile) {

        return CommonResponse.onSuccess(
                profileAwardsService.addProfileAwardsCertification(
                        accessor.getMemberId(), profileAwardsId, profileAwardsCertificationFile));
    }

    // 수상 인증 단일 삭제
    @DeleteMapping("/certification/{profileAwardsId}")
    @MemberOnly
    @Logging(
            item = "Profile_Awards",
            action = "DELETE_REMOVE_PROFILE_AWARDS_CERTIFICATION",
            includeResult = true)
    public CommonResponse<ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse>
            removeProfileAwardsCertification(
                    @Auth final Accessor accessor, @PathVariable final Long profileAwardsId) {
        return CommonResponse.onSuccess(
                profileAwardsService.removeProfileAwardsCertification(
                        accessor.getMemberId(), profileAwardsId));
    }
}
