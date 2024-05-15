package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    public final ProfileService profileService;

    // 프로필 자기소개에 해당하는 부분은 생성 과정이 필요하지 않다.

    @GetMapping("/introduction")
    @MemberOnly
    public ResponseEntity<ProfileIntroductionResponse> getProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileIntroductionResponse profileIntroductionResponse = profileService.getProfileIntroduction(profileId);
        return ResponseEntity.ok().body(profileIntroductionResponse);
    }

    @PatchMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> updateProfileIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileUpdateRequest updateRequest
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.update(profileId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.deleteIntroduction(profileId);
        return ResponseEntity.noContent().build();
    }

//    // 마이페이지 컨트롤러
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<ProfileResponse> getProfile(
//            @Auth final Accessor accessor
//    ) {
//        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
//
//        final ProfileResponse profileResponse = profileService.getProfile(profileId);
//
//        // ProfileResponse에 [3. 내 이력서]를 모두 담아야 한다.
//        return ResponseEntity.ok().body(profileResponse);
//    }
}
