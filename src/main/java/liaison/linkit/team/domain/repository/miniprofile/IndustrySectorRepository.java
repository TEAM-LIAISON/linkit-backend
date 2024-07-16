package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.IndustrySector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndustrySectorRepository extends JpaRepository<IndustrySector, Long> {

    @Query("SELECT i FROM IndustrySector i WHERE i.sectorName = :sectorName")
    IndustrySector findBySectorName(@Param("sectorName") final String sectorName);
}
