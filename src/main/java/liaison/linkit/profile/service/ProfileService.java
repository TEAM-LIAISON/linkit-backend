package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Antecedents;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.School;
import liaison.linkit.profile.domain.repository.*;
import liaison.linkit.profile.domain.repository.Attach.AttachFileRepository;
import liaison.linkit.profile.domain.repository.Attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.Education.DegreeRepository;
import liaison.linkit.profile.domain.repository.Education.EducationRepository;
import liaison.linkit.profile.domain.repository.Education.MajorRepository;
import liaison.linkit.profile.domain.repository.Education.SchoolRepository;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.profile.dto.request.DefaultProfileCreateRequest;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.*;
import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_ID;

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
    private final ProfileTeamBuildingRepository profileTeamBuildingRepository;
    private final TeamBuildingRepository teamBuildingRepository;

    // 이력 정보 담당
    private final AntecedentsRepository antecedentsRepository;

    // 학력 정보 담당
    private final EducationRepository educationRepository;
    private final SchoolRepository schoolRepository;
    private final DegreeRepository degreeRepository;
    private final MajorRepository majorRepository;
    // -> ERD부터 설계 작업, 개발 DB에 인서트 필요


    // 첨부 링크 정보 담당
    private final AttachUrlRepository attachUrlRepository;

    // 첨부 이미지(파일 경로) 정보 담당
    private final AttachFileRepository attachFileRepository;

    public Long validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        } else {
            return profileRepository.findByMemberId(memberId).getId();
        }
    }

    public void saveDefault(
            final Long memberId,
            final DefaultProfileCreateRequest defaultProfileCreateRequest)
    {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final List<TeamBuildingField> teamBuildingFields = teamBuildingRepository
                .findTeamBuildingFieldsByFieldNames(defaultProfileCreateRequest.getProfileTeamBuildingResponse().getTeamBuildingFieldNames());
        final List<Skill> skills = skillRepository.findSkillNamesBySkillNames(defaultProfileCreateRequest.getProfileSkillCreateRequest().getSkillNames());

        final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                .toList();

        final List<ProfileSkill> profileSkills = skills.stream()
                .map(skill -> new ProfileSkill(null, profile, skill))
                .toList();

        final List<Education> educations = defaultProfileCreateRequest
                .getEducationCreateRequest().stream()
                .map(request -> {
                    School school = schoolRepository.findBySchoolName(request.getSchoolName());
                    Degree degree = degreeRepository.findByDegreeName(request.getDegreeName());
                    Major major = majorRepository.findByMajorName(request.getMajorName());
                    return request.toEntity(profile, school, degree, major);
                }).collect(Collectors.toList());


        final List<Antecedents> antecedents = defaultProfileCreateRequest
                .getAntecedentsCreateRequest().stream().map(request -> request.toEntity(profile)).toList();

        profileTeamBuildingRepository.saveAll(profileTeamBuildingFields);
        profileSkillRepository.saveAll(profileSkills);
        educationRepository.saveAll(educations);
        antecedentsRepository.saveAll(antecedents);

    }

    @Transactional(readOnly = true)
    public ProfileIntroductionResponse getProfileIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));
        return ProfileIntroductionResponse.profileIntroduction(profile);
    }

    public void update(final Long profileId, final ProfileUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        // 사용자가 입력한 정보로 업데이트한다.
        profile.update(updateRequest);

        // 저장되었으므로, 완성도 상태를 변경한다.
        profile.updateIsIntroduction(true);

        // 상태 판단 함수가 필요하다 -> +- 20에 따른 상태 판단 진행
        profile.updateMemberProfileTypeByCompletion();

        // DB에 저장한다.
        profileRepository.save(profile);
    }

    public void deleteIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        profile.deleteIntroduction();
        profile.updateIsIntroduction(false);
        profile.updateMemberProfileTypeByCompletion();

        profileRepository.save(profile);
    }

    public ProfileResponse getProfile(
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileSkillResponse profileSkillResponse,
            final ProfileTeamBuildingResponse profileTeamBuildingResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses,
            final AttachResponse attachResponse
    ) {
        return ProfileResponse.profileItems(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileSkillResponse,
                profileTeamBuildingResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );
    }


}
