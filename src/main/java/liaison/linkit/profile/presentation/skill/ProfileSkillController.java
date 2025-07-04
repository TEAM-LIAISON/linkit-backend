package liaison.linkit.profile.presentation.skill;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileSkillService;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/skill")
@Slf4j
public class ProfileSkillController {

    private final ProfileSkillService profileSkillService;

    @GetMapping
    @MemberOnly
    @Logging(item = "Profile_Skill", action = "GET_PROFILE_SKILL_ITEMS", includeResult = true)
    public CommonResponse<ProfileSkillItems> getProfileSkillItems(@Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profileSkillService.getProfileSkillItems(accessor.getMemberId()));
    }

    @PostMapping
    @MemberOnly
    @Logging(item = "Profile_Skill", action = "POST_PROFILE_SKILL_ITEMS", includeResult = true)
    public CommonResponse<ProfileSkillResponseDTO.ProfileSkillItems> updateProfileSkillItems(
            @Auth final Accessor accessor,
            @RequestBody final ProfileSkillRequestDTO.AddProfileSkillRequest profileSkillRequest) {

        return CommonResponse.onSuccess(
                profileSkillService.updateProfileSkillItems(
                        accessor.getMemberId(), profileSkillRequest));
    }
}
