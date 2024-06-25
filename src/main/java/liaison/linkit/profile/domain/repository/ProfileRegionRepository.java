package liaison.linkit.profile.domain.repository;

import liaison.linkit.region.domain.ProfileRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfileRegionRepository extends JpaRepository<ProfileRegion, Long> {

    @Query("SELECT profileRegion FROM ProfileRegion profileRegion WHERE profileRegion.profile.id = :profileId")
    Optional<ProfileRegion> findByProfileId(@Param("profileId") final Long profileId);

    boolean existsByProfileId(final Long profileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProfileRegion profileRegion WHERE profileRegion.profile.id = :profileId")
    void deleteByProfileId(@Param("profileId") final Long profileId);
}
