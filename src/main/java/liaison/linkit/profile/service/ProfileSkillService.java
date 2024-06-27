package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.response.skill.ProfileSkillResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    public void save(final Long memberId, final ProfileSkillCreateRequest profileSkillCreateRequest) {
        final Profile profile = getProfile(memberId);

        // 마찬가지로 존재하는 이력이 있는 항목이라면 삭제를 하고 저장한다.
        if (profileSkillRepository.existsByProfileId(profile.getId())) {
            profileSkillRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsProfileSkill(false);
            profile.updateMemberProfileTypeByCompletion();
        }

        List<String> roleFields = profileSkillCreateRequest.getRoleFields();
        List<String> skillNames = profileSkillCreateRequest.getSkillNames();

        // Ensure that the lists have the same length to prevent index out of bounds exception
        if (roleFields.size() != skillNames.size()) {
            throw new IllegalArgumentException("The number of roles and skills must be the same.");
        }

        List<ProfileSkill> profileSkills = new ArrayList<>();
        for (int i = 0; i < roleFields.size(); i++) {
            String roleField = roleFields.get(i);
            String skillName = skillNames.get(i);

            Skill skill = skillRepository.findByRoleFieldAndSkillName(roleField, skillName);

            ProfileSkill profileSkill = new ProfileSkill(null, profile, skill, roleField, skillName);
            profileSkills.add(profileSkill);
        }

        profileSkillRepository.saveAll(profileSkills);
        profile.updateIsProfileSkill(true);
        profile.updateMemberProfileTypeByCompletion();
    }

    @Transactional(readOnly = true)
    public ProfileSkillResponse getAllProfileSkills(final Long memberId) {
        final Profile profile = getProfile(memberId);
        log.info("Profile Skill 조회 문제 확인 여부");

        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profile.getId());

        log.info("profileSkills={}", profileSkills);

// 역할 필드와 기술 이름을 각각의 리스트로 생성
        List<String> roleFields = profileSkills.stream()
                .map(profileSkill -> profileSkill.getRoleField()) // 가정: ProfileSkill에 roleField라는 필드가 있다.
                .collect(Collectors.toList());

        List<String> skillNames = profileSkills.stream()
                .map(profileSkill -> {
                    Skill skill = skillRepository.findById(profileSkill.getSkill().getId())
                            .orElseThrow(() -> new RuntimeException("Skill not found"));
                    return skill.getSkillName();
                }).collect(Collectors.toList());

        return ProfileSkillResponse.of(roleFields, skillNames);
    }


//    public void updateSkill(final Long memberId, final ProfileSkillUpdateRequest updateRequest) {
//        final Profile profile = profileRepository.findByMemberId(memberId);
//        final Long profileSkillId = validateProfileSkillByMember(memberId);
//        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profile.getId());
//
//        final List<Skill> skills = skillRepository
//                .findSkillNamesBySkillNames(updateRequest.getSkillNames());
//
//        profileSkillRepository.deleteAll(profileSkills);
//
//        final List<ProfileSkill> newProfileSkills = skills.stream()
//                .map(skill -> new ProfileSkill(null, profile, skill))
//                .toList();
//
//        profileSkillRepository.saveAll(newProfileSkills);
//    }
}
