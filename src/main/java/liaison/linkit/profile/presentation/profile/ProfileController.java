package liaison.linkit.profile.presentation.profile;

import liaison.linkit.member.business.MemberService;
import liaison.linkit.profile.service.AntecedentsService;
import liaison.linkit.profile.service.ProfileAwardsService;
import liaison.linkit.profile.service.EducationService;
import liaison.linkit.profile.service.MiniProfileService;
import liaison.linkit.profile.service.ProfileService;
import liaison.linkit.profile.service.ProfileSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ProfileController {

    public final MemberService memberService;

    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;

    public final ProfileSkillService profileSkillService;

    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final ProfileAwardsService profileAwardsService;

}
