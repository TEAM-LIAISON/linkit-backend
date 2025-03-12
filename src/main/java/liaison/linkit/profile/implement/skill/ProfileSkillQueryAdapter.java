package liaison.linkit.profile.implement.skill;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileSkillQueryAdapter {
    private final ProfileSkillRepository profileSkillRepository;

    public List<ProfileSkill> getProfileSkills(final Long memberId) {
        return profileSkillRepository.getProfileSkills(memberId);
    }

    public boolean existsByProfileId(final Long profileId) {
        return profileSkillRepository.existsByProfileId(profileId);
    }
}
