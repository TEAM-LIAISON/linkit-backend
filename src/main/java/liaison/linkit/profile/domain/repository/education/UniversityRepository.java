package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.common.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long>, UniversityCustomRepository {
}
