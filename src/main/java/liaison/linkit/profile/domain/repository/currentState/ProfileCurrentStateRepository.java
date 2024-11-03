package liaison.linkit.profile.domain.repository.currentState;

import liaison.linkit.profile.domain.ProfileCurrentState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileCurrentStateRepository extends JpaRepository<ProfileCurrentState, Long>, ProfileCurrentStateCustomRepository {
}
