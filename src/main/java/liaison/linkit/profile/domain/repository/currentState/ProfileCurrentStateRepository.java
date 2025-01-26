package liaison.linkit.profile.domain.repository.currentState;

import liaison.linkit.profile.domain.state.ProfileCurrentState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileCurrentStateRepository extends JpaRepository<ProfileCurrentState, Long>, ProfileCurrentStateCustomRepository {
}
