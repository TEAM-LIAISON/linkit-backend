package liaison.linkit.profile.implement.skill;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileSkillCommandAdapter {
    private final ProfileSkillRepository profileSkillRepository;

    public void removeProfileSkillsByProfileId(final Long profileId) {
        profileSkillRepository.deleteAllByProfileId(profileId);
    }

    public void addProfileSkills(final List<ProfileSkill> profileSkills) {
        profileSkillRepository.saveAll(profileSkills);
    }
}
