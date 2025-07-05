package liaison.linkit.profile.domain.repository.currentState;

import liaison.linkit.common.domain.ProfileState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileStateRepository
        extends JpaRepository<ProfileState, Long>, ProfileStateCustomRepository {}
