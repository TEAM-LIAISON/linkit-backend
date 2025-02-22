package liaison.linkit.profile.domain.repository.skill;

import java.util.List;

import liaison.linkit.profile.domain.skill.ProfileSkill;

public interface ProfileSkillCustomRepository {
    boolean existsByProfileId(final Long profileId);

    List<ProfileSkill> getProfileSkills(final Long memberId);

    void deleteAllByProfileId(final Long profileId);
}
