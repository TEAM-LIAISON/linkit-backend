package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import java.util.Map;

import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;

public interface ProjectRoleContributionCustomRepository {
    Map<Long, List<String>> getProjectRolesByProfileId(final Long profileId);

    List<ProjectRoleContribution> getProjectRoleContributions(final Long profilePortfolioId);
}
