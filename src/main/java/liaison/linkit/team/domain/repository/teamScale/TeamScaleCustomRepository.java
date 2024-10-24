package liaison.linkit.team.domain.repository.teamScale;

import liaison.linkit.team.domain.scale.TeamScale;

public interface TeamScaleCustomRepository {
    TeamScale findByScaleName(final String scaleName);
}
