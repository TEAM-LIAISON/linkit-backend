package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.profile.business.ProfileSkillMapper;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.implement.ProfileSkillQueryAdapter;
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

    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileSkillMapper profileSkillMapper;

    @Transactional(readOnly = true)
    public ProfileSkillResponseDTO.ProfileSkillItems getProfileSkillItems(final Long memberId) {
        log.info("memberId = {}의 내 스킬 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(memberId);
        log.info("profileSkills = {}가 성공적으로 조회되었습니다.", profileSkills);

        return profileSkillMapper.toProfileSkillItems(profileSkills);
    }
}
