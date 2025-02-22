package liaison.linkit.profile.domain.repository.skill;

import liaison.linkit.profile.domain.skill.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileSkillRepository
        extends JpaRepository<ProfileSkill, Long>, ProfileSkillCustomRepository {}
