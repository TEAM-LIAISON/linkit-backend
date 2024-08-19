package liaison.linkit.team.domain.repository.activity.method;

import liaison.linkit.team.domain.activity.ActivityMethod;

import java.util.List;

public interface ActivityMethodRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
    List<ActivityMethod> findAllByTeamProfileId(final Long teamProfileId);
}
