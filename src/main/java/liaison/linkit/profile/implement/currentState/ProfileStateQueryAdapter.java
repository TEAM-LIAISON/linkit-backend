package liaison.linkit.profile.implement.currentState;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.profile.domain.repository.currentState.ProfileStateRepository;
import liaison.linkit.profile.exception.state.ProfileStateNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileStateQueryAdapter {
    private final ProfileStateRepository profileStateRepository;

    public ProfileState findByStateName(final String stateName) {
        return profileStateRepository.findByStateName(stateName).orElseThrow(() -> ProfileStateNotFoundException.EXCEPTION);
    }
}
