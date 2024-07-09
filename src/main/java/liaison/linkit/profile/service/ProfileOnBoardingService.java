package liaison.linkit.profile.service;

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
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.onBoarding.OnBoardingProfileResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileOnBoardingService {

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

    @Transactional(readOnly = true)
    public ProfileOnBoardingIsValueResponse getProfileOnBoardingIsValue(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileOnBoardingIsValueResponse.profileOnBoardingIsValue(profile);
    }

    public OnBoardingProfileResponse getOnBoardingProfile(
            // 1. 희망 팀빌딩 분야
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            // 2. 지역 및 위치 정보
            final ProfileRegionResponse profileRegionResponse,
            // 3. 희망하는 역할 및 보유 기술
            final JobAndSkillResponse jobAndSkillResponse,
            // 4. 학교 정보
            final List<EducationResponse> educationResponses,
            // 5. 이력 정보
            final List<AntecedentsResponse> antecedentsResponses,
            // 6. 미니 프로필 정보
            final MiniProfileResponse miniProfileResponse
    ) {
        return OnBoardingProfileResponse.onBoardingProfileItems(
                profileTeamBuildingFieldResponse,
                profileRegionResponse,
                jobAndSkillResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );
    }

    // 1.5.4. 희망 직무/역할 저장 메서드
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

    // 1.5.3. 보유 기술 저장 메서드
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
                .findSkillsBySkillNames(skillNames);

        final List<ProfileSkill> profileSkills = skills.stream()
                .map(skill -> new ProfileSkill(null, profile, skill))
                .toList();

        profileSkillRepository.saveAll(profileSkills);
        profile.updateIsProfileSkill(true);
        log.info("OnBoardingService savePersonalSkill 메서드가 종료됩니다.");
    }

    // 1.5.3. 보유 역할 및 기술 응답 구현부
    public JobAndSkillResponse getJobAndSkill(
            final Long memberId
    ) {
        final Profile profile = getProfileByMember(memberId);
        List<ProfileJobRole> profileJobRoles = profileJobRoleRepository.findAllByProfileId(profile.getId());
        List<String> jobRoleNames = profileJobRoles.stream()
                .map(profileJobRole -> jobRoleRepository.findById(profileJobRole.getJobRole().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(JobRole::getJobRoleName)
                .toList();

        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profile.getId());
        List<String> skillNames = profileSkills.stream()
                .map(profileSkill -> skillRepository.findById(profileSkill.getSkill().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Skill::getSkillName)
                .toList();

        return JobAndSkillResponse.of(jobRoleNames, skillNames);
    }


    // 해당 내 이력서 기준으로 상태 업데이트
    public void updateMemberProfileType(
            final Long memberId
    ) {
        final Profile profile = getProfileByMember(memberId);
        profile.updateMemberProfileTypeByCompletion();
    }


}
