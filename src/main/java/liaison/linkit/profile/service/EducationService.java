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

            final ProfileEducation newProfileEducation = ProfileEducation.of(
                    profile,
                    request.getAdmissionYear(),
                    request.getGraduationYear(),
                    request.getUniversityName(),
                    request.getMajorName()
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
}
