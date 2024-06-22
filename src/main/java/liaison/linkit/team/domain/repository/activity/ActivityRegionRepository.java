package liaison.linkit.team.domain.repository.activity;

import liaison.linkit.region.domain.ActivityRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ActivityRegionRepository extends JpaRepository<ActivityRegion, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional  // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM ActivityRegion activityRegion WHERE activityRegion.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT activityRegion FROM ActivityRegion activityRegion WHERE activityRegion.teamProfile.id = :teamProfileId")
    Optional<ActivityRegion> findByTeamProfileId(@Param("teamProfileId")final Long teamProfileId);
}
