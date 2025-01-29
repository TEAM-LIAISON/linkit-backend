package liaison.linkit.profile.implement.activity;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.repository.activity.ProfileActivityRepository;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.UpdateProfileActivityRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileActivityCommandAdapter {

    final ProfileActivityRepository profileActivityRepository;

    public ProfileActivity addProfileActivity(final ProfileActivity profileActivity) {
        return profileActivityRepository.save(profileActivity);
    }

    public void removeProfileActivity(final ProfileActivity profileActivity) {
        profileActivityRepository.delete(profileActivity);
    }

    public ProfileActivity updateProfileActivity(final Long profileActivityId, final UpdateProfileActivityRequest updateProfileActivityRequest) {
        return profileActivityRepository.updateProfileActivity(profileActivityId, updateProfileActivityRequest);
    }

    public void removeProfileActivitiesByProfileId(final Long profileId) {
        profileActivityRepository.removeProfileActivitiesByProfileId(profileId);
    }
}
