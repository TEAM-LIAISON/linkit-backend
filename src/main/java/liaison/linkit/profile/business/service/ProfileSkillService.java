package liaison.linkit.profile.business.service;

import java.util.List;
import java.util.stream.Collectors;

import liaison.linkit.profile.business.mapper.ProfileSkillMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillCommandAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.profile.implement.skill.SkillQueryAdapter;
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
    private final SkillQueryAdapter skillQueryAdapter;

    private final ProfileSkillCommandAdapter profileSkillCommandAdapter;

    private final ProfileSkillMapper profileSkillMapper;

    @Transactional(readOnly = true)
    public ProfileSkillResponseDTO.ProfileSkillItems getProfileSkillItems(final Long memberId) {

        final List<ProfileSkill> profileSkills =
                profileSkillQueryAdapter.getProfileSkills(memberId);

        return profileSkillMapper.toProfileSkillItems(profileSkills);
    }

    public ProfileSkillResponseDTO.ProfileSkillItems updateProfileSkillItems(
            final Long memberId,
            final ProfileSkillRequestDTO.AddProfileSkillRequest addProfileSkillRequest) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 기존에 저장 이력이 존재하는 경우
        if (profileSkillQueryAdapter.existsByProfileId(profile.getId())) {
            // 기존 이력을 모두 삭제한다.
            profileSkillCommandAdapter.removeProfileSkillsByProfileId(profile.getId());
            profile.setIsProfileSkill(false);
            profile.removeProfileSkillCompletion();
        }

        // 새로운 스킬 이력 생성
        List<ProfileSkill> profileSkills =
                addProfileSkillRequest.getProfileSkillItems().stream()
                        .map(
                                requestItem -> {
                                    // Skill 엔티티 조회
                                    Skill skill =
                                            skillQueryAdapter.getSkillBySkillName(
                                                    requestItem.getSkillName());

                                    // ProfileSkill 매핑
                                    return profileSkillMapper.toProfileSkill(
                                            profile, skill, requestItem);
                                })
                        .collect(Collectors.toList());

        // ProfileSkill 저장
        profileSkillCommandAdapter.addProfileSkills(profileSkills);

        // 스킬 이력 존재 여부에 따라 프로필 업데이트
        if (!profileSkills.isEmpty()) {
            profile.setIsProfileSkill(true);
            profile.addProfileSkillCompletion();
        }

        return profileSkillMapper.toProfileSkillItems(profileSkills);
    }
}
