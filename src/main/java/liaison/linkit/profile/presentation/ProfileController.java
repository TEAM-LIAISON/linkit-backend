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
    private final ProfileService profileService;

    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileResponse> getProfile(@Auth final Accessor accessor) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileResponse profileResponse = profileService.getProfileDetail(profileId);
        return ResponseEntity.ok().body(profileResponse);
    }

    @PutMapping
    @MemberOnly
    public ResponseEntity<Void> updateProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileUpdateRequest profileUpdateRequest
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.update(profileId, profileUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
