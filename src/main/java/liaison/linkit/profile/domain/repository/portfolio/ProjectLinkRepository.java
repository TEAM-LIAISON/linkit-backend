package liaison.linkit.profile.domain.repository.portfolio;

import liaison.linkit.profile.domain.portfolio.ProjectLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectLinkRepository
        extends JpaRepository<ProjectLink, Long>, ProjectLinkCustomRepository {}
