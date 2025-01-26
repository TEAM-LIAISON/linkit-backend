package liaison.linkit.profile.domain.repository.education;

import java.util.Optional;
import liaison.linkit.common.domain.University;

public interface UniversityCustomRepository {
    Optional<University> findUniversityByUniversityName(final String universityName);
}
