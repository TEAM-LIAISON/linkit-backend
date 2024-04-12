package liaison.linkit.profile.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mini-profile")
public class MiniProfileController {
    private final MiniProfileService miniProfileService;

    // 해당 회원이 가지고 있는 미니 프로필 정보를 가져옴
    @GetMapping
    @MemberOnly
    public ResponseEntity<MiniProfileResponse> getMiniProfile(@Auth final Accessor accessor) {
        Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        final MiniProfileResponse miniProfileResponse = miniProfileService.getMiniProfileDetail(miniProfileId);
        return ResponseEntity.ok().body(miniProfileResponse);
    }


}
