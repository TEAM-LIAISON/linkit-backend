package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;

@Mapper
public class ProfileSkillMapper {

    public ProfileSkillResponseDTO.ProfileSkillItem toProfileSkillItem(final ProfileSkill profileSkill) {
        return ProfileSkillResponseDTO.ProfileSkillItem
                .builder()
                .profileSkillId(profileSkill.getId())
                .skillName(profileSkill.getSkill().getSkillName())
                .skillLevel(profileSkill.getSkillLevel())
                .build();
    }

    public ProfileSkillResponseDTO.ProfileSkillItems toProfileSkillItems(final List<ProfileSkill> profileSkills) {
        List<ProfileSkillItem> items = profileSkills.stream()
                .map(this::toProfileSkillItem)
                .toList();

        return ProfileSkillResponseDTO.ProfileSkillItems
                .builder()
                .profileSkillItems(items)
                .build();
    }

    public List<ProfileSkillItem> profileSkillsToProfileSkillItems(final List<ProfileSkill> profileSkills) {
        return profileSkills.stream()
                .map(this::toProfileSkillItem)
                .collect(Collectors.toList());
    }

    public ProfileSkill toProfileSkill(
            final Profile profile,
            final Skill skill,
            final ProfileSkillRequestDTO.AddProfileSkillItem addProfileSkillItem) {
        return ProfileSkill.builder()
                .profile(profile)
                .skill(skill)
                .skillLevel(addProfileSkillItem.getSkillLevel())
                .build();
    }

}
