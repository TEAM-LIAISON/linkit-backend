package liaison.linkit.profile.service.onBoarding;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.repository.jobRole.JobRoleRepository;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OnBoardingService {

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;
    private final JobRoleRepository jobRoleRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;

    // 내 이력서 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 멤버 아이디로 내 이력서 유효성을 검증하는 로직
    public void validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        }
    }

    public void savePersonalJobAndRole(
            final Long memberId,
            final List<String> jobRoleNames
    ) {
        log.info("OnBoardingService savePersonalJobAndRole 메서드가 실행됩니다.");

        final Profile profile = getProfileByMember(memberId);
        if (profileJobRoleRepository.existsByProfileId(profile.getId())) {
            profileJobRoleRepository.deleteAllByProfileId(profile.getId());
        }

        final List<JobRole> jobRoles = jobRoleRepository
                .findJobRoleByJobRoleNames(jobRoleNames);

        final List<ProfileJobRole> profileJobRoles = jobRoles.stream()
                .map(jobRole -> new ProfileJobRole(null, profile, jobRole))
                .toList();

        profileJobRoleRepository.saveAll(profileJobRoles);
        profile.updateIsProfileJobRole(true);
        log.info("OnBoardingService savePersonalJobAndRole 메서드가 종료됩니다.");
    }

    public void savePersonalSkill(
            final Long memberId,
            final List<String> skillNames
    ) {
        log.info("OnBoardingService savePersonalSkill 메서드가 실행됩니다.");
        final Profile profile = getProfileByMember(memberId);
        if (profileSkillRepository.existsByProfileId(profile.getId())) {
            profileSkillRepository.deleteAllByProfileId(profile.getId());
        }

        final List<Skill> skills = skillRepository
                .findSkillNamesBySkillNames(skillNames);

        final List<ProfileSkill> profileSkills = skills.stream()
                .map(skill -> new ProfileSkill(null, profile, skill))
                .toList();

        profileSkillRepository.saveAll(profileSkills);
        profile.updateIsProfileSkill(true);
        log.info("OnBoardingService savePersonalSkill 메서드가 종료됩니다.");
    }
}
