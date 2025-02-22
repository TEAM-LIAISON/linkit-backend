package liaison.linkit.profile.domain.repository.position;

import liaison.linkit.profile.domain.position.ProfilePosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePositionRepository
        extends JpaRepository<ProfilePosition, Long>, ProfilePositionCustomRepository {}
