package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import java.util.Map;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.repository.portfolio.ProjectRoleContributionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectRoleContributionQueryAdapter {
    private final ProjectRoleContributionRepository projectRoleContributionRepository;

    public List<ProjectRoleContribution> getProjectRoleContributions(
            final Long profilePortfolioId) {
        return projectRoleContributionRepository.getProjectRoleContributions(profilePortfolioId);
    }

    public Map<Long, List<String>> getProjectRolesByProfileId(final Long profileId) {
        return projectRoleContributionRepository.getProjectRolesByProfileId(profileId);
    }
}
