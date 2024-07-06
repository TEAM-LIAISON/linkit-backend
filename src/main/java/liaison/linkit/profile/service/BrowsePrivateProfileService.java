package liaison.linkit.profile.service;

import liaison.linkit.profile.domain.Profile;
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
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public BrowsePrivateProfileResponse getPrivateProfile(
            final Long miniProfileId
    ) {
        final Profile profile = getProfileByMiniProfile(miniProfileId);
        return BrowsePrivateProfileResponse.privateProfile(profile);
    }
}
