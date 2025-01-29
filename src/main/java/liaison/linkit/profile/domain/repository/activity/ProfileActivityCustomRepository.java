package liaison.linkit.profile.domain.repository.activity;

import java.util.List;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.UpdateProfileActivityRequest;

public interface ProfileActivityCustomRepository {
    List<ProfileActivity> getProfileActivities(final Long memberId);

    ProfileActivity updateProfileActivity(final Long profileActivityId, final UpdateProfileActivityRequest updateProfileActivityRequest);

    boolean existsByProfileId(final Long profileId);

    void removeProfileActivitiesByProfileId(final Long profileId);
}
