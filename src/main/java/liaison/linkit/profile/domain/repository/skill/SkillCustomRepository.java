package liaison.linkit.profile.domain.repository.skill;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.domain.skill.Skill;

public interface SkillCustomRepository {
    List<Skill> findSkillsBySkillNames(final List<String> skillNames);

    Optional<Skill> getSkillBySkillName(final String skillName);
}
