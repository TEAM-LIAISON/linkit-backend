package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.repository.attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.education.MajorRepository;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_MINI_PROFILE_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MINI_PROFILE_BY_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class BrowsePrivateProfileService {

    private final ProfileRepository profileRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;
    private final AntecedentsRepository antecedentsRepository;
    private final EducationRepository educationRepository;
    private final UniversityRepository universityRepository;
    private final DegreeRepository degreeRepository;
    private final MajorRepository majorRepository;
    private final AttachUrlRepository attachUrlRepository;


    // 미니 프로필로 해당 내 이력서의 유효성 판단
    public void validatePrivateProfileByMiniProfile(final Long miniProfileId) {
        if (!miniProfileRepository.existsById(miniProfileId)) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        }
    }

    private Profile getProfileByMiniProfileId(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }

    public Long getTargetMemberIdByMiniProfileId(final Long miniProfileId) {

    }
}
