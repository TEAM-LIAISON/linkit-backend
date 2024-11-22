package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import java.util.Map;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.repository.portfolio.ProfilePortfolioRepository;
import liaison.linkit.profile.domain.repository.portfolio.ProjectRoleContributionRepository;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSkillRepository;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSubImageRepository;
import liaison.linkit.profile.exception.portfolio.PortfolioNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePortfolioQueryAdapter {

    private final ProfilePortfolioRepository profilePortfolioRepository;
    private final ProjectRoleContributionRepository projectRoleContributionRepository;
    private final ProjectSubImageRepository projectSubImageRepository;
    private final ProjectSkillRepository projectSkillRepository;

    public ProfilePortfolio getProfilePortfolio(final Long profilePortfolioId) {
        return profilePortfolioRepository.getProfilePortfolio(profilePortfolioId)
                .orElseThrow(() -> PortfolioNotFoundException.EXCEPTION);
    }

    public List<ProfilePortfolio> getProfilePortfolios(final Long profileId) {
        return profilePortfolioRepository.getProfilePortfolios(profileId);
    }

    public Map<Long, List<String>> getProjectRolesByProfileId(final Long profileId) {
        return projectRoleContributionRepository.getProjectRolesByProfileId(profileId);
    }

    public List<ProjectRoleContribution> getProjectRoleContributions(final Long profilePortfolioId) {
        return projectRoleContributionRepository.getProjectRoleContributions(profilePortfolioId);
    }

    public List<String> getProjectSubImagePaths(final Long profilePortfolioId) {
        return projectSubImageRepository.getProjectSubImagePaths(profilePortfolioId);
    }

    public List<ProjectSkill> getProjectSkills(final Long profilePortfolioId) {
        return projectSkillRepository.getProjectSkills(profilePortfolioId);
    }
}
