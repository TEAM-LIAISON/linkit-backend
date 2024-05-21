package liaison.linkit.team.domain.repository.Activity;

import liaison.linkit.team.domain.activity.ActivityRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityRegionRepository extends JpaRepository<ActivityRegion, Long> {

    @Query("SELECT ar FROM ActivityRegion ar WHERE ar.regionName = :regionName")
    ActivityRegion findActivityRegionByRegionName(@Param("regionName") String regionName);


}
