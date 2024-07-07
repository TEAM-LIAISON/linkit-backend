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
import liaison.linkit.profile.dto.response.MemberNameResponse;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

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

    // 미니 프로필 ID로 해당 내 이력서 ID 조회
    public Long getTargetPrivateProfileIdByMiniProfileId(final Long miniProfileId) {
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        return profile.getId();
    }

    // 미니 프로필로 해당 내 이력서 조회
    private Profile getProfileByMiniProfileId(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }

    // 프로필 (내 이력서) 1개 조회
    private Profile getProfile(final Long browseTargetPrivateProfileId) {
        return profileRepository.findById(browseTargetPrivateProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
    }

    @Transactional(readOnly = true)
    public ProfileIsValueResponse getProfileIsValue(final Long browseTargetPrivateProfileId) {
        final Profile profile = getProfile(browseTargetPrivateProfileId);
        return ProfileIsValueResponse.profileIsValue(profile);
    }

    public ProfileResponse getProfileResponse(
            final MiniProfileResponse miniProfileResponse,
            final MemberNameResponse memberNameResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final JobAndSkillResponse jobAndSkillResponse,
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses,
            final AttachResponse attachResponse
    ) {
        return ProfileResponse.profileItems(
                miniProfileResponse,
                memberNameResponse,
                completionResponse,
                profileIntroductionResponse,
                jobAndSkillResponse,
                profileTeamBuildingFieldResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );
    }
}
