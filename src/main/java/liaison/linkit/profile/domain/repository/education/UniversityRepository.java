package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UniversityRepository extends JpaRepository<University, Long> {

    University findByUniversityName(@Param("universityName") final String universityName);
}
