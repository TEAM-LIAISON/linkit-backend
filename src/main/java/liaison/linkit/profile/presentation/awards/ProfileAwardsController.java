package liaison.linkit.profile.presentation.awards;

import liaison.linkit.profile.service.ProfileAwardsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ProfileAwardsController {
    private final ProfileAwardsService profileAwardsService;
}
