package liaison.linkit.profile.presentation.skill;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.service.ProfileSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/skill")
public class ProfileSkillController {

    private ProfileSkillService profileSkillService;

    @GetMapping
    @MemberOnly
    public CommonResponse<ProfileSkillResponseDTO.ProfileSkillItems> getProfileSkillItems(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(profileSkillService.getProfileSkillItems(accessor.getMemberId()));
    }
}
