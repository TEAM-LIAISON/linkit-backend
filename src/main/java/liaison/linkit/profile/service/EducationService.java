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
import liaison.linkit.profile.dto.response.EducationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_EDUCATION_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_EDUCATION_ID;

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

    public void validateEducationByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!educationRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_EDUCATION_WITH_MEMBER);
        }
//        else {
//            // 여러 개 찾을 수 있음
//            return educationRepository.findByProfileId(profileId).getId();
//        }
    }

    public List<EducationResponse> save(final Long memberId, final List<EducationCreateRequest> educationCreateRequests) {
        List<EducationResponse> responses = new ArrayList<>();

        final Profile profile = profileRepository.findByMemberId(memberId);
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found for memberId: " + memberId);
        }

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
        profile.updateIsEducation(true);
        return responses;
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
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final List<Education> educations = educationRepository.findAllByProfileId(profileId);
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
