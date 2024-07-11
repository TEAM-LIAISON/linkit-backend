package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.University;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.education.MajorRepository;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
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

    private Education getEducation(final Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EDUCATION_ID));
    }

    // 멤버로부터 프로필 아이디를 조회해서 학력 정보 존재성을 판단
    public void validateEducationByMember(final Long memberId) {
        if (!educationRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_EDUCATIONS_BY_PROFILE_ID);
        }
    }

    public void save(
            final Long memberId,
            final List<EducationCreateRequest> educationCreateRequests
    ) {
        final Profile profile = getProfile(memberId);

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

    @Transactional(readOnly = true)
    public List<EducationResponse> getAllEducations(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<Education> educations = educationRepository.findAllByProfileId(profile.getId());
        return educations.stream()
                .map(this::getEducationResponse)
                .toList();
    }

    private EducationResponse getEducationResponse(final Education education) {
        return EducationResponse.of(education);
    }

    public void delete(final Long memberId, final Long educationId) {
        log.info("삭제 메서드 실행");
        final Profile profile = getProfile(memberId);
        final Education education = getEducation(educationId);

        educationRepository.deleteById(education.getId());
        log.info("삭제 완료");
        if (!educationRepository.existsByProfileId(profile.getId())) {
            profile.updateIsEducation(false);
            profile.cancelPerfectionDefault();
            profile.updateMemberProfileTypeByCompletion();
        }
    }



}
