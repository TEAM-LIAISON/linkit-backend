package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.repository.portfolio.ProjectRoleContributionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectRoleContributionCommandAdapter {
    private final ProjectRoleContributionRepository projectRoleContributionRepository;

    public List<ProjectRoleContribution> addProjectRoleContributions(final List<ProjectRoleContribution> projectRoleContributions) {
        return projectRoleContributionRepository.saveAll(projectRoleContributions);
    }

    public void deleteAll(final List<ProjectRoleContribution> projectRoleContributions) {
        projectRoleContributionRepository.deleteAll(projectRoleContributions);
    }
}
