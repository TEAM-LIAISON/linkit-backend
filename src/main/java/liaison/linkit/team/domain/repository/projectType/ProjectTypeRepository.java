package liaison.linkit.team.domain.repository.projectType;

import liaison.linkit.team.domain.projectType.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTypeRepository
        extends JpaRepository<ProjectType, Long>, ProjectTypeCustomRepository {}
