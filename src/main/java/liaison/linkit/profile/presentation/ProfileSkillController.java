package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.response.ProfileSkillResponse;
import liaison.linkit.profile.service.ProfileSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile_skill")
public class ProfileSkillController {

    private final ProfileSkillService profileSkillService;

    // 보유 기술 생성
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createProfileSkill(
            @Auth final Accessor accessor,
            @RequestBody @Valid ProfileSkillCreateRequest profileSkillCreateRequest
    ) {
        profileSkillService.save(accessor.getMemberId(), profileSkillCreateRequest);
        return ResponseEntity.ok().build();
    }

    // 보유 기술 전체 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileSkillResponse> getProfileSkillList(
            @Auth final Accessor accessor
    ) {
        profileSkillService.validateProfileSkillByMember(accessor.getMemberId());
        final ProfileSkillResponse profileSkillResponses = profileSkillService.getAllProfileSkills(accessor.getMemberId());
        return ResponseEntity.ok().body(profileSkillResponses);
    }

//    @PatchMapping
//    @MemberOnly
//    public ResponseEntity<Void> updateProfileSkill(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid final ProfileSkillUpdateRequest updateRequest
//    ) {
//        profileSkillService.updateSkill(accessor.getMemberId(), updateRequest);
//        return ResponseEntity.noContent().build();
//    }
    
}
