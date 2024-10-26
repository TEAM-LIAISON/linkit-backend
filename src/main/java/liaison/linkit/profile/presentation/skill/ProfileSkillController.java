package liaison.linkit.profile.presentation.skill;

import liaison.linkit.profile.service.ProfileSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/skill")
public class ProfileSkillController {

    private ProfileSkillService profileSkillService;

//    @GetMapping
//    @MemberOnly
//    public CommonResponse<ProfileSkillResponseDTO.ProfileSkillItems> getProfileSkillItems(
//            @Auth final Accessor accessor
//    ) {
//        return CommonResponse.onSuccess(profileSkillService.getProfileSkillItems(accessor.getMemberId()));
//    }
}
