package liaison.linkit.profile.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;

@Mapper
public class ProfileSkillMapper {

    public ProfileSkillResponseDTO.ProfileSkillItem toProfileSkillItem(final ProfileSkill profileSkill) {
        return ProfileSkillResponseDTO.ProfileSkillItem
                .builder()
                .profileSkillId(profileSkill.getId())
                .skillIconImagePath(profileSkill.getSkillIconImagePath())
                .skillName(profileSkill.getSkillName())
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


}
