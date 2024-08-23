package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Degree;

import java.util.Optional;

public interface DegreeRepositoryCustom {
    Optional<Degree> findByDegreeName(final String degreeName);
}
