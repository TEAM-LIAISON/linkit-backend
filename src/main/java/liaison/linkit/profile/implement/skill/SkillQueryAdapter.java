package liaison.linkit.profile.implement.skill;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.skill.SkillRepository;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.exception.skill.SkillNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class SkillQueryAdapter {
    private final SkillRepository skillRepository;

    public List<Skill> getSkillsBySkillNames(final List<String> skillNames) {
        return skillRepository.findSkillsBySkillNames(skillNames);
    }

    public Skill getSkillBySkillName(final String skillName) {
        return skillRepository
                .getSkillBySkillName(skillName)
                .orElseThrow(() -> SkillNotFoundException.EXCEPTION);
    }
}
