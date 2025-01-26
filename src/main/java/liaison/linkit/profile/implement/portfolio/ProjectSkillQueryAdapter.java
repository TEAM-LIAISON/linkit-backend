package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectSkillQueryAdapter {
    private final ProjectSkillRepository projectSkillRepository;

    public List<ProjectSkill> getProjectSkills(final Long profilePortfolioId) {
        return projectSkillRepository.getProjectSkills(profilePortfolioId);
    }
}
