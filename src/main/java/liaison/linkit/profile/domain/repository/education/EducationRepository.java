package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long>, EducationRepositoryCustom {

}
