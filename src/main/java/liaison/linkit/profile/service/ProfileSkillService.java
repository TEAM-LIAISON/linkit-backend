package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.skill.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSkillService {

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 어떤 보유 기술 및 역할 1개만 조회할 때
    private ProfileSkill getProfileSkill(final Long profileSkillId) {
        return profileSkillRepository.findById(profileSkillId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_SKILL_BY_ID));
    }

    // 해당 내 이력서로부터 모든 보유 기술들을 조회하려고 할 때
    private List<ProfileSkill> getProfileSkills(final Long profileId) {
        try {
            return profileSkillRepository.findAllByProfileId(profileId);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_PROFILE_SKILLS_BY_PROFILE_ID);
        }
    }

    // 멤버로부터 프로필 아이디를 조회해서 존재성을 판단
    public void validateProfileSkillByMember(final Long memberId) {
        if (!profileSkillRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_PROFILE_SKILLS_BY_PROFILE_ID);
        }
    }


}
