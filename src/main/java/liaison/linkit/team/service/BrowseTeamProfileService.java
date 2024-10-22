package liaison.linkit.team.service;

import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrowseTeamProfileService {

    private final ProfileRepository profileRepository;
    
}

