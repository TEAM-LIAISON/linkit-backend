package liaison.linkit.profile.presentation.education;

import liaison.linkit.profile.service.ProfileEducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/education")
@Slf4j
public class ProfileEducationController {

    private final ProfileEducationService profileEducationService;

}
