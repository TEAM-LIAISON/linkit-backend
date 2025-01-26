package liaison.linkit.team.domain.repository.region;

import liaison.linkit.profile.domain.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long>, RegionRepositoryCustom {

}
