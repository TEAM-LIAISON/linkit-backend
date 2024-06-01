package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DegreeRepository extends JpaRepository<Degree, Long> {

    @Query("SELECT d FROM Degree d WHERE d.degreeName = :degreeName")
    Degree findByDegreeName(@Param("degreeName") final String degreeName);
}
