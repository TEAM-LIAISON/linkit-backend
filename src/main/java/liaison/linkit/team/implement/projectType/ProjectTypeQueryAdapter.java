package liaison.linkit.team.implement.projectType;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.projectType.ProjectType;
import liaison.linkit.team.domain.repository.projectType.ProjectTypeRepository;
import liaison.linkit.team.exception.projectType.ProjectTypeNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectTypeQueryAdapter {
    private final ProjectTypeRepository projectTypeRepository;

    public ProjectType findByProjectTypeName(final String projectTypeName) {
        return projectTypeRepository
                .findByProjectTypeName(projectTypeName)
                .orElseThrow(() -> ProjectTypeNotFoundException.EXCEPTION);
    }
}
