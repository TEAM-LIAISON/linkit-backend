package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface SchoolRepository extends JpaRepository<School, Long> {

    School findBySchoolName(@Param("schoolName") final String schoolName);
}
