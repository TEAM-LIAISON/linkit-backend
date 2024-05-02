package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    public final ProfileService profileService;

    @PutMapping
    @MemberOnly
    public ResponseEntity<Void> updateProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileUpdateRequest updateRequest
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.update(profileId, updateRequest);
        return ResponseEntity.noContent().build();
    }
}
