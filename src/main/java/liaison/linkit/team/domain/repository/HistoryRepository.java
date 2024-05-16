package liaison.linkit.team.domain.repository;

import liaison.linkit.team.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    History findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
