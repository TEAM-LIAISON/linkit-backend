package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.TeamScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamScaleRepository extends JpaRepository<TeamScale, Long> {

    @Query("SELECT t FROM TeamScale t WHERE t.sizeType = :sizeType")
    TeamScale findBySizeType(@Param("sizeType") final String sizeType);
}
