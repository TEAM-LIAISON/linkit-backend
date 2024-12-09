package liaison.linkit.team.domain.repository.region;

import java.util.Optional;
import liaison.linkit.team.domain.TeamRegion;

public interface TeamRegionCustomRepository {
    boolean existsTeamRegionByTeamId(final Long teamId);

    Optional<TeamRegion> findTeamRegionByTeamId(final Long teamId);
}
