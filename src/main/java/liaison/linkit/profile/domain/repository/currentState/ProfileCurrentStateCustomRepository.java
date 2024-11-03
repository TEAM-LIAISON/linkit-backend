package liaison.linkit.profile.domain.repository.currentState;

import java.util.List;
import liaison.linkit.profile.domain.ProfileCurrentState;

public interface ProfileCurrentStateCustomRepository {
    List<ProfileCurrentState> findByProfileId(final Long profileId);
}
