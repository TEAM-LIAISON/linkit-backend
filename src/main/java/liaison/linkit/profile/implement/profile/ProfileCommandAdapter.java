package liaison.linkit.profile.implement.profile;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileCommandAdapter {

    final ProfileRepository profileRepository;

    public Profile create(final Profile profile) {
        return profileRepository.save(profile);
    }
}
