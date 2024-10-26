package liaison.linkit.profile.presentation;

import liaison.linkit.profile.service.AntecedentsService;
import liaison.linkit.profile.service.ProfileAwardsService;
import liaison.linkit.profile.service.BrowsePrivateProfileService;
import liaison.linkit.profile.service.EducationService;
import liaison.linkit.profile.service.MiniProfileService;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class BrowsePrivateProfileController {


    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;

    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final ProfileAwardsService profileAwardsService;

    public final BrowsePrivateProfileService browsePrivateProfileService;
}
