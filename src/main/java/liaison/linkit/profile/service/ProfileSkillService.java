package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.request.skill.ProfileSkillUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileSkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_SKILL_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileSkillService {

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;

    public Long validateProfileSkillByMember(final Long memberId) {
        final Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileSkillRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_SKILL_WITH_MEMBER);
        } else {
            return profileSkillRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(final Long memberId, final ProfileSkillCreateRequest profileSkillCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        if (profileSkillRepository.existsByProfileId(profile.getId())) {
            // 존재했으면 삭제부터
            profileSkillRepository.deleteAllByProfileId(profile.getId());

            final List<Skill> skills = skillRepository
                    .findSkillNamesBySkillNames(profileSkillCreateRequest.getSkillNames());

            final List<ProfileSkill> profileSkills = skills.stream()
                    .map(skill -> new ProfileSkill(null, profile, skill))
                    .toList();

            profileSkillRepository.saveAll(profileSkills);
        } else {
            // 존재하지 않았으면 그냥 저장
            final List<Skill> skills = skillRepository
                    .findSkillNamesBySkillNames(profileSkillCreateRequest.getSkillNames());

            final List<ProfileSkill> profileSkills = skills.stream()
                    .map(skill -> new ProfileSkill(null, profile, skill))
                    .toList();

            profileSkillRepository.saveAll(profileSkills);
        }

        profile.updateIsProfileSkill(true);
    }

    @Transactional(readOnly = true)
    public ProfileSkillResponse getAllProfileSkills(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profileId);

        List<String> profileSkillNames = profileSkills.stream()
                .map(profileSkill -> skillRepository.findById(profileSkill.getProfile().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Skill::getSkillName)
                .toList();

        return new ProfileSkillResponse(profileSkillNames);
    }


    public void updateSkill(final Long memberId, final ProfileSkillUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long profileSkillId = validateProfileSkillByMember(memberId);
        List<ProfileSkill> profileSkills = profileSkillRepository.findAllByProfileId(profile.getId());

        final List<Skill> skills = skillRepository
                .findSkillNamesBySkillNames(updateRequest.getSkillNames());

        profileSkillRepository.deleteAll(profileSkills);

        final List<ProfileSkill> newProfileSkills = skills.stream()
                .map(skill -> new ProfileSkill(null, profile, skill))
                .toList();

        profileSkillRepository.saveAll(newProfileSkills);
    }
}
