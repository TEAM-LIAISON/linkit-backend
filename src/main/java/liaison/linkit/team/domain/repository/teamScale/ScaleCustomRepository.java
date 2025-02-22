package liaison.linkit.team.domain.repository.teamScale;

import java.util.Optional;

import liaison.linkit.team.domain.scale.Scale;

public interface ScaleCustomRepository {

    Optional<Scale> findByScaleName(final String scaleName);
}
