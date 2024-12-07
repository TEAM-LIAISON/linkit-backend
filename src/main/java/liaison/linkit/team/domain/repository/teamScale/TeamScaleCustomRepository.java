package liaison.linkit.team.domain.repository.teamScale;

import liaison.linkit.team.domain.scale.TeamScale;

public interface TeamScaleCustomRepository {
    boolean existsTeamScaleByTeamId(final Long teamId);

    TeamScale findTeamScaleByTeamId(final Long teamId);
}
