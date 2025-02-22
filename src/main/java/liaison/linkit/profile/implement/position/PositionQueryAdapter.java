package liaison.linkit.profile.implement.position;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.common.domain.Position;
import liaison.linkit.profile.domain.repository.position.PositionRepository;
import liaison.linkit.profile.exception.position.PositionNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class PositionQueryAdapter {
    final PositionRepository positionRepository;

    public Position findByMajorPositionAndSubPosition(
            final String majorPosition, final String subPosition) {
        return positionRepository
                .findByMajorPositionAndSubPosition(majorPosition, subPosition)
                .orElseThrow(() -> PositionNotFoundException.EXCEPTION);
    }
}
