package liaison.linkit.profile.domain.repository.portfolio;

import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSkillRepository
        extends JpaRepository<ProjectSkill, Long>, ProjectSkillCustomRepository {}
