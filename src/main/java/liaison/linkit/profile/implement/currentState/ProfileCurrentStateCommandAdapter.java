package liaison.linkit.profile.implement.currentState;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.repository.currentState.ProfileCurrentStateRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileCurrentStateCommandAdapter {
    final ProfileCurrentStateRepository profileCurrentStateRepository;

    public void deleteAllByProfileId(final Long profileId) {
        profileCurrentStateRepository.deleteAllByProfileId(profileId);
    }

    public void saveAll(final List<ProfileCurrentState> profileCurrentStates) {
        profileCurrentStateRepository.saveAll(profileCurrentStates);
    }
}
