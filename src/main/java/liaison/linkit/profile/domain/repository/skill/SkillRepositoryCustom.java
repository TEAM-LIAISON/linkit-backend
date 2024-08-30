package liaison.linkit.profile.domain.repository.skill;

import liaison.linkit.profile.domain.skill.Skill;

import java.util.List;

public interface SkillRepositoryCustom {
    List<Skill> findSkillsBySkillNames(final List<String> skillNames);
}
