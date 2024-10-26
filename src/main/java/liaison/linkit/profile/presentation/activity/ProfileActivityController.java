package liaison.linkit.profile.presentation.activity;

import liaison.linkit.profile.service.ProfileActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ProfileActivityController {

    private final ProfileActivityService profileActivityService;

    
}
