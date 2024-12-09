package liaison.linkit.profile.domain.repository.currentState;

import java.util.List;
import liaison.linkit.profile.domain.state.ProfileCurrentState;

public interface ProfileCurrentStateCustomRepository {
    List<ProfileCurrentState> findProfileCurrentStatesByProfileId(final Long profileId);

    boolean existsProfileCurrentStateByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
