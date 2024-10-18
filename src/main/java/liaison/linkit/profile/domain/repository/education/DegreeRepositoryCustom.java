package liaison.linkit.profile.domain.repository.education;

import java.util.Optional;

public interface DegreeRepositoryCustom {
    Optional<Degree> findByDegreeName(final String degreeName);
}
