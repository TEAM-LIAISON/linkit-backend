package liaison.linkit.profile.service;

import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSkillService {


    @Transactional(readOnly = true)
    public ProfileSkillResponseDTO.ProfileSkillItems getProfileSkillItems(final Long memberId) {
        return;
    }
}
