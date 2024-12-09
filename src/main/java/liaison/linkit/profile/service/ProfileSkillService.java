package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.profile.business.ProfileSkillMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillCommandAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO;
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

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileSkillCommandAdapter profileSkillCommandAdapter;
    private final ProfileSkillMapper profileSkillMapper;

    @Transactional(readOnly = true)
    public ProfileSkillResponseDTO.ProfileSkillItems getProfileSkillItems(final Long memberId) {
        log.info("memberId = {}의 내 스킬 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(memberId);
        log.info("profileSkills = {}가 성공적으로 조회되었습니다.", profileSkills);

        return profileSkillMapper.toProfileSkillItems(profileSkills);
    }

    public ProfileSkillResponseDTO.ProfileSkillItems updateProfileSkillItems(final Long memberId, final ProfileSkillRequestDTO.AddProfileSkillRequest addProfileSkillRequest) {
        log.info("memberId = {}의 내 스킬 Items 수정 요청이 발생했습니다.", memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 기존에 저장 이력이 존재하는 경우
        if (profileSkillQueryAdapter.existsByProfileId(profile.getId())) {
            // 기존 이력을 모두 삭제한다.
            profileSkillCommandAdapter.removeProfileSkillsByProfileId(profile.getId());
            profile.setIsProfileSkill(false);
            profile.removeProfileSkillCompletion();
        }

        List<ProfileSkill> profileSkills = addProfileSkillRequest.getProfileSkillItems().stream()
                .map(requestItem -> profileSkillMapper.toProfileSkill(profile, requestItem))
                .toList();

        profileSkillCommandAdapter.addProfileSkills(profileSkills);

        if (profileSkillQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfileSkill(true);
            profile.addProfileSkillCompletion();
        }

        return profileSkillMapper.toProfileSkillItems(profileSkills);
    }
}
