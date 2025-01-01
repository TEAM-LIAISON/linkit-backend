package liaison.linkit.team.domain.repository.region;

import java.util.Optional;
import liaison.linkit.team.domain.region.TeamRegion;

public interface TeamRegionCustomRepository {
    boolean existsTeamRegionByTeamId(final Long teamId);

    Optional<TeamRegion> findTeamRegionByTeamId(final Long teamId);

    void deleteAllByTeamId(final Long teamId);
}
