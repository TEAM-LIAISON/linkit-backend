package liaison.linkit.profile.service;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_DEGREE_NAME;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_EDUCATIONS_BY_PROFILE_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_EDUCATION_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private ProfileEducation getEducation(final Long educationId) {
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
        }

        List<EducationResponse> responses = new ArrayList<>();

        // 반복문을 통해 들어온 순서대로 저장한다.
        for (EducationCreateRequest request : educationCreateRequests) {
            final Degree degree = degreeRepository.findByDegreeName(request.getDegreeName())
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

            final ProfileEducation newProfileEducation = ProfileEducation.of(
                    profile,
                    request.getAdmissionYear(),
                    request.getGraduationYear(),
                    request.getUniversityName(),
                    request.getMajorName(),
                    degree
            );

            ProfileEducation savedProfileEducation = educationRepository.save(newProfileEducation);
            responses.add(getEducationResponse(savedProfileEducation));
        }

        // 반복문 종료 후에 프로필의 상태를 업데이트한다.
        profile.updateIsEducation(true);
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getAllEducations(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<ProfileEducation> profileEducations = educationRepository.findAllByProfileId(profile.getId());
        return profileEducations.stream()
                .map(this::getEducationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getAllBrowseEducations(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

        final List<ProfileEducation> profileEducations = educationRepository.findAllByProfileId(profile.getId());
        return profileEducations.stream()
                .map(this::getEducationResponse)
                .toList();
    }

    private EducationResponse getEducationResponse(final ProfileEducation profileEducation) {
        return EducationResponse.of(profileEducation);
    }

    public void delete(final Long memberId, final Long educationId) {
        log.info("삭제 메서드 실행");
        final Profile profile = getProfile(memberId);
        final ProfileEducation profileEducation = getEducation(educationId);

        // 해당 학력 삭제
        educationRepository.deleteById(profileEducation.getId());

        log.info("삭제 완료");
        if (!educationRepository.existsByProfileId(profile.getId())) {
            profile.updateIsEducation(false);
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
            return makeNewEducation(educationCreateRequest, profile);
        }
    }

    public Long update(
            final Long educationId,
            final EducationCreateRequest educationCreateRequest
    ) {
        final ProfileEducation profileEducation = getEducation(educationId);

        final Degree degree = degreeRepository.findByDegreeName(educationCreateRequest.getDegreeName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

        profileEducation.update(educationCreateRequest, educationCreateRequest.getUniversityName(),
                educationCreateRequest.getMajorName(), degree);
        return profileEducation.getId();
    }

    private Long makeNewEducation(
            final EducationCreateRequest educationCreateRequest,
            final Profile profile
    ) {
        final Degree degree = degreeRepository.findByDegreeName(educationCreateRequest.getDegreeName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_DEGREE_NAME));

        final ProfileEducation newProfileEducation = ProfileEducation.of(
                profile,
                educationCreateRequest.getAdmissionYear(),
                educationCreateRequest.getGraduationYear(),
                educationCreateRequest.getUniversityName(),
                educationCreateRequest.getMajorName(),
                degree
        );

        return educationRepository.save(newProfileEducation).getId();
    }
}
