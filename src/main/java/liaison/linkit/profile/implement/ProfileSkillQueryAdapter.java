package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
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
}
