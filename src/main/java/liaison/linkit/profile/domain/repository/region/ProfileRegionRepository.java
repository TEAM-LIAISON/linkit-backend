package liaison.linkit.profile.domain.repository.region;

import liaison.linkit.profile.domain.region.ProfileRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRegionRepository extends JpaRepository<ProfileRegion, Long>, ProfileRegionRepositoryCustom {

}
