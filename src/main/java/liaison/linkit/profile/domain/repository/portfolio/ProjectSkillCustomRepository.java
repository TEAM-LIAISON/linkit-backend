package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;

public interface ProjectSkillCustomRepository {
    List<ProjectSkill> getProjectSkills(final Long profilePortfolioId);
}
