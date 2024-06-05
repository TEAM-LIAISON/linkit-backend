package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.ProfileRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRegionRepository extends JpaRepository<ProfileRegion, Long> {

    Optional<ProfileRegion> findByProfileId(@Param("profileId") final Long profileId);

    boolean existsByProfileId(final Long profileId);

}
