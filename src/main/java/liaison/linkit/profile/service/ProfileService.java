package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.*;
import liaison.linkit.profile.domain.repository.attach.AttachFileRepository;
import liaison.linkit.profile.domain.repository.attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.education.MajorRepository;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.dto.request.IntroductionCreateRequest;
import liaison.linkit.profile.dto.response.*;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;


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
    private final AttachFileRepository attachFileRepository;

    // 프로필 (내 이력서) 1개 조회
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

    public void deleteIntroduction(final Long memberId) {
        // 프로필 조회
        final Profile profile = getProfileByMember(memberId);

        profile.deleteIntroduction();
        profile.updateIsIntroduction(false);
        profile.updateMemberProfileTypeByCompletion();

        profileRepository.save(profile);
    }

    public ProfileResponse getProfileResponse(
            final MiniProfileResponse miniProfileResponse,
            final MemberNameResponse memberNameResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileSkillResponse profileSkillResponse,
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
                profileSkillResponse,
                profileTeamBuildingFieldResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );
    }


    public OnBoardingProfileResponse getOnBoardingProfile(
            // 1. 희망 팀빌딩 분야
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            // 2. 희망하는 역할
            final ProfileSkillResponse profileSkillResponse,
            // 3. 지역 및 위치 정보
            final ProfileRegionResponse profileRegionResponse,
            // 4. 학교 정보
            final List<EducationResponse> educationResponses,
            // 5. 이력 정보
            final List<AntecedentsResponse> antecedentsResponses,
            // 6. 미니 프로필 정보
            final MiniProfileResponse miniProfileResponse,

            final MemberNameResponse memberNameResponse
    ) {
        return OnBoardingProfileResponse.onBoardingProfileItems(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse,
                memberNameResponse
        );
    }

    public void saveIntroduction(
            final Long memberId,
            final IntroductionCreateRequest introductionCreateRequest
    ) {
        final Profile profile = getProfileByMember(memberId);
        profile.updateIntroduction(introductionCreateRequest.getIntroduction());
    }
}
