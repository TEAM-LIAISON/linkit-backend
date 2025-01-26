package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.ProfileEducation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileEducationRepository extends JpaRepository<ProfileEducation, Long>, ProfileEducationCustomRepository {

}
