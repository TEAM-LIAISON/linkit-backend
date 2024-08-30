package liaison.linkit.team.domain.repository.activity.region;

import liaison.linkit.team.domain.activity.ActivityRegion;

import java.util.Optional;

public interface ActivityRegionRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
    Optional<ActivityRegion> findByTeamProfileId(final Long teamProfileId);
}
