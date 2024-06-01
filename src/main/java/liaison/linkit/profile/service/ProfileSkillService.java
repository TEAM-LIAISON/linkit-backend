package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.response.ProfileSkillResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_SKILL_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSkillService {

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;

    public void validateProfileSkillByMember(final Long memberId) {
        final Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileSkillRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_SKILL_WITH_MEMBER);
        }
    }

    public void save(final Long memberId, final ProfileSkillCreateRequest profileSkillCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        if (profileSkillRepository.existsByProfileId(profile.getId())) {
            profileSkillRepository.deleteAllByProfileId(profile.getId());
        }

        final List<ProfileSkill> profileSkills = profileSkillCreateRequest.getSkillPairs().stream()
                .map(skillPair -> {
                    Skill skill = skillRepository.findByRoleFieldAndSkillName(skillPair.getRoleField(),skillPair.getSkillName());
                    return new ProfileSkill(null, profile, skill, skillPair.getRoleField(), skillPair.getSkillName());
                }).toList();

        profileSkillRepository.saveAll(profileSkills);

        profile.updateIsProfileSkill(true);
    }

    @Transactional(readOnly = true)
    public ProfileSkillResponse getAllProfileSkills(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        log.info("Profile Skill 조회 문제 확인 여부");

        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profileId);

        log.info("profileSkills={}", profileSkills);

        List<String> profileSkillNames = profileSkills.stream()
                .map(profileSkill -> skillRepository.findById(profileSkill.getSkill().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Skill::getSkillName)
                .toList();

        return new ProfileSkillResponse(profileSkillNames);
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
