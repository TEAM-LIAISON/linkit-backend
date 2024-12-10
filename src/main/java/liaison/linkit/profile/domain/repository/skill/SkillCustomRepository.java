package liaison.linkit.profile.domain.repository.skill;

import java.util.Optional;
import liaison.linkit.profile.domain.skill.Skill;

import java.util.List;

public interface SkillCustomRepository {
    List<Skill> findSkillsBySkillNames(final List<String> skillNames);

    Optional<Skill> getSkillBySkillName(final String skillName);
}
