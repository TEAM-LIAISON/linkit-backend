package liaison.linkit.profile.domain.repository.Education;

import liaison.linkit.profile.domain.education.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DegreeRepository extends JpaRepository<Degree, Long> {
    Degree findByDegreeName(@Param("degreeName") final String degreeName);
}
