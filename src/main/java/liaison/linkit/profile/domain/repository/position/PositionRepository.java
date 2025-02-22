package liaison.linkit.profile.domain.repository.position;

import liaison.linkit.common.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository
        extends JpaRepository<Position, Long>, PositionCustomRepository {}
