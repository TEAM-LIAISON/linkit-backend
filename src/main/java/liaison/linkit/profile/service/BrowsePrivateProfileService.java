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
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
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

        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        log.info("validatePrivateProfileByMiniProfile 내에서 미니 프로필 객체를 조회하였습니다.");
        if (!profileRepository.existsById(miniProfile.getProfile().getId())) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        }
    }

    // 미니 프로필 ID로 해당 내 이력서 ID 조회
    public Long getTargetPrivateProfileIdByMiniProfileId(final Long miniProfileId) {
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        log.info("열람하고자 하는 내 이력서의 ID는 profileI={}입니다.", profile.getId());
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

    public BrowsePrivateProfileResponse getProfileResponse(
            final Long profileId,
            final MiniProfileResponse miniProfileResponse,
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
        return BrowsePrivateProfileResponse.privateProfile(
                profileId,
                miniProfileResponse,
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
