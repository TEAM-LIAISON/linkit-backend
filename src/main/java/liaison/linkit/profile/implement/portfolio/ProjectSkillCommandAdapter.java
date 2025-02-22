package liaison.linkit.profile.implement.portfolio;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectSkillCommandAdapter {
    private final ProjectSkillRepository projectSkillRepository;

    public void saveAll(final List<ProjectSkill> projectSkills) {
        projectSkillRepository.saveAll(projectSkills);
    }

    public void deleteAll(final List<ProjectSkill> projectSkills) {
        projectSkillRepository.deleteAll(projectSkills);
    }
}
