package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.University;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.education.MajorRepository;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.dto.request.education.EducationUpdateRequest;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EducationService {

    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final UniversityRepository universityRepository;
    private final DegreeRepository degreeRepository;
    private final MajorRepository majorRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 어떤 학력 정보 1개만 조회할 때
    private Education getEducation(final Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EDUCATION_BY_ID));
    }

    // 내 이력서 하나에 대한 모든 학력 정보 리스트 조회
    private List<Education> getEducations(final Long profileId) {
        try {
            return educationRepository.findAllByProfileId(profileId);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_EDUCATIONS_BY_PROFILE_ID);
        }
    }

    // 멤버로부터 프로필 아이디를 조회해서 학력 정보 존재성을 판단
    public void validateEducationByMember(final Long memberId) {
        if (!educationRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_EDUCATIONS_BY_PROFILE_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    public void save(final Long memberId, final List<EducationCreateRequest> educationCreateRequests) {

        final Profile profile = getProfile(memberId);

        // 교육항목 존재 이력이 있다면, 우선 전체 삭제
        if (educationRepository.existsByProfileId(profile.getId())) {
            educationRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsEducation(false);
            profile.updateMemberProfileTypeByCompletion();
        }

        List<EducationResponse> responses = new ArrayList<>();

        // 반복문을 통해 들어온 순서대로 저장한다.
        for (EducationCreateRequest request : educationCreateRequests) {
            final University university = universityRepository.findByUniversityName(request.getUniversityName());
            if (university == null) {
                throw new IllegalArgumentException("University not found: " + request.getUniversityName());
            }

            final Degree degree = degreeRepository.findByDegreeName(request.getDegreeName());
            if (degree == null) {
                throw new IllegalArgumentException("Degree not found: " + request.getDegreeName());
            }

            final Major major = majorRepository.findByMajorName(request.getMajorName());
            if (major == null) {
                throw new IllegalArgumentException("Major not found: " + request.getMajorName());
            }

            final Education newEducation = Education.of(
                    profile,
                    request.getAdmissionYear(),
                    request.getGraduationYear(),
                    university,
                    degree,
                    major
            );

            Education savedEducation = educationRepository.save(newEducation);
            responses.add(getEducationResponse(savedEducation));
        }

        // 반복문 종료 후에 프로필의 상태를 업데이트한다.
        profile.updateIsEducation(true);
        profile.updateMemberProfileTypeByCompletion();
    }


    private EducationResponse getEducationResponse(final Education education) {
        return EducationResponse.of(education);
    }

    @Transactional(readOnly = true)
    public EducationResponse getEducationDetail(final Long educationId) {
        final Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EDUCATION_ID));

        return EducationResponse.personalEducation(education);
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getAllEducations(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<Education> educations = educationRepository.findAllByProfileId(profile.getId());
        return educations.stream()
                .map(this::getEducationResponse)
                .toList();
    }

    public EducationResponse update(final Long educationId, final EducationUpdateRequest educationUpdateRequest) {

        final Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EDUCATION_ID));

        final University university = universityRepository.findByUniversityName(educationUpdateRequest.getUniversityName());
        final Degree degree = degreeRepository.findByDegreeName(educationUpdateRequest.getDegreeName());
        final Major major = majorRepository.findByMajorName(educationUpdateRequest.getMajorName());

        education.update(educationUpdateRequest, university, major, degree);
        educationRepository.save(education);
        return getEducationResponse(education);
    }

    public void delete(final Long memberId, final Long educationId) {
        if (!educationRepository.existsById(educationId)) {
            throw new BadRequestException(NOT_FOUND_EDUCATION_ID);
        }
        educationRepository.deleteById(educationId);
    }

}
