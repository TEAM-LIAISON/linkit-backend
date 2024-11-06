package liaison.linkit.profile.implement.activity;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.repository.activity.ProfileActivityRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileActivityCommandAdapter {

    final ProfileActivityRepository profileActivityRepository;

    public ProfileActivity addProfileActivity(final ProfileActivity profileActivity) {
        return profileActivityRepository.save(profileActivity);
    }
}
