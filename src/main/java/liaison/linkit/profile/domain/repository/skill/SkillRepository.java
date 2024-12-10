package liaison.linkit.profile.domain.repository.skill;

import liaison.linkit.profile.domain.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long>, SkillCustomRepository {

}
