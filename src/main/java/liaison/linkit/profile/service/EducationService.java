package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.repository.EducationRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.EducationCreateRequest;
import liaison.linkit.profile.dto.request.EducationUpdateRequest;
import liaison.linkit.profile.dto.response.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_EDUCATION_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_EDUCATION_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationService {

    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;

    public Long validateEducationByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!educationRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_EDUCATION_WITH_MEMBER);
        } else {
            return educationRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(final Long memberId, final EducationCreateRequest educationCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final Education newEducation = Education.of(
                profile,
                educationCreateRequest.getAdmissionYear(),
                educationCreateRequest.getAdmissionMonth(),
                educationCreateRequest.getGraduationYear(),
                educationCreateRequest.getGraduationMonth(),
                educationCreateRequest.getEducationDescription(),
                educationCreateRequest.getSchool(),
                educationCreateRequest.getDegree(),
                educationCreateRequest.getMajor()
        );
        educationRepository.save(newEducation);
        // 학력 항목이 기입되었음을 나타냄 -> true 전달
        profile.updateIsEducation(true);
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

    public void update(final Long educationId, final EducationUpdateRequest educationUpdateRequest) {
        final Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EDUCATION_ID));

        education.update(educationUpdateRequest);
        educationRepository.save(education);
    }

    public void delete(final Long educationId) {
        if (!educationRepository.existsById(educationId)) {
            throw new BadRequestException(NOT_FOUND_EDUCATION_ID);
        }
        educationRepository.deleteById(educationId);
    }

}
