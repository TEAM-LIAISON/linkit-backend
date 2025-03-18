package liaison.linkit.team.domain.repository.projectType;

import java.util.Optional;

import liaison.linkit.team.domain.projectType.ProjectType;

public interface ProjectTypeCustomRepository {

    Optional<ProjectType> findByProjectTypeName(final String projectTypeName);
}
