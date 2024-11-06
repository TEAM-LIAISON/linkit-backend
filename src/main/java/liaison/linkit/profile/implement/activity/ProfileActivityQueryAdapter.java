package liaison.linkit.profile.implement.activity;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.repository.activity.ProfileActivityRepository;
import liaison.linkit.profile.exception.activity.ProfileActivityNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileActivityQueryAdapter {

    private final ProfileActivityRepository profileActivityRepository;

    public List<ProfileActivity> getProfileActivities(final Long memberId) {
        return profileActivityRepository.getProfileActivities(memberId);
    }

    public ProfileActivity getProfileActivity(final Long profileActivityId) {
        return profileActivityRepository.findById(profileActivityId)
                .orElseThrow(() -> ProfileActivityNotFoundException.EXCEPTION);
    }
}
