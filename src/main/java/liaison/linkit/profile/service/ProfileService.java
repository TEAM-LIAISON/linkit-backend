package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
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
import liaison.linkit.profile.dto.request.IntroductionRequest;
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
public class ProfileService {
    // 프로필 리포지토리 -> 자기소개, 회원 정보 담당
    private final ProfileRepository profileRepository;
    // 미니 프로필 정보 담당
    private final MiniProfileRepository miniProfileRepository;
    // 보유 기술 정보 담당
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;
    // 희망 팀빌딩 분야 정보 담당
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    // 이력 정보 담당
    private final AntecedentsRepository antecedentsRepository;

    // 학력 정보 담당
    private final EducationRepository educationRepository;
    private final UniversityRepository universityRepository;
    private final DegreeRepository degreeRepository;
    private final MajorRepository majorRepository;

    // 첨부 링크 정보 담당
    private final AttachUrlRepository attachUrlRepository;
    
    // 첨부 이미지(파일 경로) 정보 담당
//    private final AttachFileRepository attachFileRepository;

    // 프로필 (내 이력서) 1개 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    private MiniProfile getMiniProfileByMember(final Long profileId) {
        return miniProfileRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_PROFILE_ID));
    }

    // 멤버 아이디로 내 이력서 유효성을 검증하는 로직
    public void validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        }
    }



    // 생성/수정/삭제 포함
    public void saveIntroduction(
            final Long memberId,
            final IntroductionRequest introductionRequest
    ) {
        final Profile profile = getProfileByMember(memberId);
        profile.updateIntroduction(introductionRequest.getIntroduction());
    }

    @Transactional(readOnly = true)
    public ProfileIsValueResponse getProfileIsValue(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileIsValueResponse.profileIsValue(profile);
    }

    @Transactional(readOnly = true)
    public ProfileIntroductionResponse getProfileIntroduction(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileIntroductionResponse.profileIntroduction(profile);
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




    private Profile getProfileByMiniProfile(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }
}
