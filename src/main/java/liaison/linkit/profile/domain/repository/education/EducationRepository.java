package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.ProfileEducation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<ProfileEducation, Long>, EducationRepositoryCustom {

}
