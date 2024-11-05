package liaison.linkit.profile.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileSkillQueryAdapter {
    private final ProfileSkillRepository profileSkillRepository;

    public ProfileSkillResponseDTO.ProfileSkillItems findProfileSkillItems(final Long memberId) {
        return profileSkillRepository.findProfileSkillItemsDTO(memberId);
    }

    public List<ProfileSkill> getProfileSkills(final Long memberId) {
        return profileSkillRepository.getProfileSkills(memberId);
    }
}
