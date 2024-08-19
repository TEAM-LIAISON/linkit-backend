package liaison.linkit.team.domain.repository.activity.region;

import liaison.linkit.team.domain.activity.ActivityRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRegionRepository extends JpaRepository<ActivityRegion, Long>, ActivityRegionRepositoryCustom {

}
