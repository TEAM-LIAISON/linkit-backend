package liaison.linkit.team.domain.repository.history;

import liaison.linkit.team.domain.history.History;

import java.util.List;
import java.util.Optional;

public interface HistoryRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    Optional<History> findByTeamProfileId(final Long teamProfileId);
    List<History> findAllByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
}
