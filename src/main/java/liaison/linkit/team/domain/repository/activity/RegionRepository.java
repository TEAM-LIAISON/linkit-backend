package liaison.linkit.team.domain.repository.activity;

import liaison.linkit.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT r FROM Region r WHERE r.cityName = :cityName AND r.divisionName = :divisionName")
    Region findRegionByCityNameAndDivisionName(@Param("cityName") final String cityName, final String divisionName);

}
