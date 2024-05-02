package liaison.linkit.profile.presentation;


import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileResponse> getProfile(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileResponse profileResponse = profileService.getProfileDetail(profileId);
        return ResponseEntity.ok().body(profileResponse);
    }

    @PatchMapping
    @MemberOnly
    public ResponseEntity<Void> updateProfile(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid final ProfileUpdateRequest updateRequest
    ) {
        log.info("실행 여부 판단");
//        Long profileId = profileService.validateProfileByMember(1L);
//        profileService.update(profileId, profileUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
