package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
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
    private final DegreeRepository degreeRepository;


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

    // 멤버로부터 프로필 아이디를 조회해서 학력 정보 존재성을 판단
    public void validateEducationByProfile(final Long profileId) {
        if (!educationRepository.existsByProfileId(profileId)) {
            throw new AuthException(NOT_FOUND_EDUCATIONS_BY_PROFILE_ID);
        }
    }

    public void saveAll(
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
            final Degree degree = degreeRepository.findByDegreeName(request.getDegreeName())
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

            final Education newEducation = Education.of(
                    profile,
                    request.getAdmissionYear(),
                    request.getGraduationYear(),
                    request.getUniversityName(),
                    request.getMajorName(),
                    degree
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

    @Transactional(readOnly = true)
    public List<EducationResponse> getAllBrowseEducations(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

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

        // 해당 학력 삭제
        educationRepository.deleteById(education.getId());

        log.info("삭제 완료");
        if (!educationRepository.existsByProfileId(profile.getId())) {
            profile.updateIsEducation(false);
            profile.updateMemberProfileTypeByCompletion();
        }
    }

    // 단일 저장
    public Long save(
            final Long memberId,
            final EducationCreateRequest educationCreateRequest
    ) {
        final Profile profile = getProfile(memberId);

        // 기존에 학력 정보가 존재했던 경우
        if (profile.getIsEducation()) {
            return makeNewEducation(educationCreateRequest, profile);
        } else {
            // 기존에 존재하지 않았던 경우
            profile.updateIsEducation(true);
            profile.updateMemberProfileTypeByCompletion();
            return makeNewEducation(educationCreateRequest, profile);
        }
    }

    public Long update(
            final Long educationId,
            final EducationCreateRequest educationCreateRequest
    ) {
        final Education education = getEducation(educationId);

        final Degree degree = degreeRepository.findByDegreeName(educationCreateRequest.getDegreeName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

        education.update(educationCreateRequest, educationCreateRequest.getUniversityName(), educationCreateRequest.getMajorName(), degree);
        return education.getId();
    }

    private Long makeNewEducation(
            final EducationCreateRequest educationCreateRequest,
            final Profile profile
    ) {
        final Degree degree = degreeRepository.findByDegreeName(educationCreateRequest.getDegreeName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

        final Education newEducation = Education.of(
                profile,
                educationCreateRequest.getAdmissionYear(),
                educationCreateRequest.getGraduationYear(),
                educationCreateRequest.getUniversityName(),
                educationCreateRequest.getMajorName(),
                degree
        );

        return educationRepository.save(newEducation).getId();
    }
}
