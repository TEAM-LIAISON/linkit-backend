package liaison.linkit.profile.implement.state;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.domain.repository.currentState.ProfileCurrentStateRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileCurrentStateQueryAdapter {
    private final ProfileCurrentStateRepository profileCurrentStateRepository;

    public boolean existsProfileCurrentStateByProfileId(final Long profileId) {
        return profileCurrentStateRepository.existsProfileCurrentStateByProfileId(profileId);
    }

    public List<ProfileCurrentState> findProfileCurrentStatesByProfileId(final Long profileId) {
        return profileCurrentStateRepository.findProfileCurrentStatesByProfileId(profileId);
    }
}
