package liaison.linkit.team.implement.scale;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamScale.ScaleRepository;
import liaison.linkit.team.domain.scale.Scale;
import liaison.linkit.team.exception.scale.ScaleNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ScaleQueryAdapter {
    private final ScaleRepository scaleRepository;

    public Scale findByScaleName(final String scaleName) {
        return scaleRepository
                .findByScaleName(scaleName)
                .orElseThrow(() -> ScaleNotFoundException.EXCEPTION);
    }
}
