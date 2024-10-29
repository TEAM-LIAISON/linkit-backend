package liaison.linkit.profile.domain.repository.activity;

import java.util.List;
import liaison.linkit.profile.domain.ProfileActivity;

public interface ProfileActivityCustomRepository {
    List<ProfileActivity> getProfileActivities(final Long memberId);
}
