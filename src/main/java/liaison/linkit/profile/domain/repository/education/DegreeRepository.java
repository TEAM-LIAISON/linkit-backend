package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository extends JpaRepository<Degree, Long>, DegreeRepositoryCustom {

}
