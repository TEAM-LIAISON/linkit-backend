package liaison.linkit.team.domain.repository.region;

import liaison.linkit.team.domain.TeamRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRegionRepository extends JpaRepository<TeamRegion, Long>, TeamRegionCustomRepository {
}
