package liaison.linkit.profile.domain.repository.currentState;

import java.util.Optional;

import liaison.linkit.common.domain.ProfileState;

public interface ProfileStateCustomRepository {
    Optional<ProfileState> findByStateName(final String stateName);
}
