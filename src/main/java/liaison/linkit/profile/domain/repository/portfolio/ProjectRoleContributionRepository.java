package liaison.linkit.profile.domain.repository.portfolio;

import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRoleContributionRepository
        extends JpaRepository<ProjectRoleContribution, Long>,
                ProjectRoleContributionCustomRepository {}
