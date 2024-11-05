package liaison.linkit.profile.domain.repository.skill;

import java.util.List;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;

public interface ProfileSkillRepositoryCustom {
    boolean existsByProfileId(final Long profileId);

    ProfileSkillResponseDTO.ProfileSkillItems findProfileSkillItemsDTO(final Long memberId);

    List<ProfileSkill> getProfileSkills(final Long memberId);
//
//    Optional<ProfileSkill> findByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
