package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileResponse;
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

    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileResponse> getProfiles(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileResponse profileResponse = profileService.getProfileDetail(profileId);
        return ResponseEntity.ok().body(profileResponse);
    }

    @PatchMapping
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
        profileService.delete(profileId);
        return ResponseEntity.noContent().build();
    }
}
