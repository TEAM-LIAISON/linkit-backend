package liaison.linkit.profile.domain.repository.position;

import java.util.Optional;
import liaison.linkit.common.domain.Position;

public interface PositionCustomRepository {
    Optional<Position> findByMajorPositionAndSubPosition(String majorPosition, String subPosition);
}
