package liaison.linkit.profile.business.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import liaison.linkit.common.domain.University;
import liaison.linkit.common.validator.FileValidator;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.mapper.ProfileEducationMapper;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.education.ProfileEducationCommandAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.implement.education.UniversityQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.UpdateProfileEducationRequest;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.AddProfileEducationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.RemoveProfileEducationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.UpdateProfileEducationResponse;
import liaison.linkit.report.certification.dto.profile.education.ProfileEducationCertificationReportDto;
import liaison.linkit.report.certification.service.DiscordProfileCertificationReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileEducationService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileEducationCommandAdapter profileEducationCommandAdapter;
    private final ProfileEducationMapper profileEducationMapper;

    private final UniversityQueryAdapter universityQueryAdapter;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;
    private final DiscordProfileCertificationReportService discordProfileCertificationReportService;

    @Transactional(readOnly = true)
    public ProfileEducationResponseDTO.ProfileEducationItems getProfileEducationItems(
            final Long memberId) {
        log.info("memberId = {}의 내 학력 Items 조회 요청 발생했습니다.", memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfileEducation> profileEducations =
                profileEducationQueryAdapter.getProfileEducations(profile.getId());
        log.info("profileEducations = {}가 성공적으로 조회되었습니다.", profileEducations);

        return profileEducationMapper.toProfileEducationItems(profileEducations);
    }

    @Transactional(readOnly = true)
    public ProfileEducationResponseDTO.ProfileEducationDetail getProfileEducationDetail(
            final Long memberId, final Long profileEducationId) {
        log.info("memberId = {}의 내 학력 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfileEducation profileEducation =
                profileEducationQueryAdapter.getProfileEducation(profileEducationId);
        log.info("profileEducation = {}가 성공적으로 조회되었습니다.", profileEducation);

        return profileEducationMapper.toProfileEducationDetail(profileEducation);
    }

    public AddProfileEducationResponse addProfileEducation(
            final Long memberId,
            final ProfileEducationRequestDTO.AddProfileEducationRequest request) {
        log.info("memberId = {}의 프로필 학력 추가 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final University university =
                universityQueryAdapter.findUniversityByUniversityName(request.getUniversityName());

        final ProfileEducation profileEducation =
                profileEducationMapper.toAddProfileEducation(profile, university, request);
        final ProfileEducation savedProfileEducation =
                profileEducationCommandAdapter.addProfileEducation(profileEducation);

        // 만약 존재하지 않았다가 생긴 경우라면 true 변환 필요
        if (!profile.isProfileEducation()) {
            profile.setIsProfileEducation(true);
            profile.addProfileEducationCompletion();
        }

        return profileEducationMapper.toAddProfileEducationResponse(savedProfileEducation);
    }

    public UpdateProfileEducationResponse updateProfileEducation(
            final Long memberId,
            final Long profileEducationId,
            final UpdateProfileEducationRequest updateProfileEducationRequest) {
        log.info("memberId = {}의 프로필 학력 수정 요청이 서비스 계층에 발생했습니다.", memberId);
        final University university =
                universityQueryAdapter.findUniversityByUniversityName(
                        updateProfileEducationRequest.getUniversityName());
        final ProfileEducation updatedProfileEducation =
                profileEducationCommandAdapter.updateProfileEducation(
                        profileEducationId, university, updateProfileEducationRequest);
        return profileEducationMapper.toUpdateProfileEducationResponse(updatedProfileEducation);
    }

    public RemoveProfileEducationResponse removeProfileEducation(
            final Long memberId, final Long profileEducationId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileEducation profileEducation =
                profileEducationQueryAdapter.getProfileEducation(profileEducationId);

        if (profileEducation.getEducationCertificationAttachFilePath() != null) {
            s3Uploader.deleteS3File(profileEducation.getEducationCertificationAttachFilePath());
            profileEducation.setProfileEducationCertification(false, false, null, null);
        }

        profileEducationCommandAdapter.removeProfileEducation(profileEducation);

        if (!profileEducationQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfileEducation(false);
            profile.removeProfileEducationCompletion();
        }

        return profileEducationMapper.toRemoveProfileEducation(profileEducationId);
    }

    public ProfileEducationResponseDTO.ProfileEducationCertificationResponse
            addProfileEducationCertification(
                    final Long memberId,
                    final Long profileEducationId,
                    final MultipartFile profileEducationCertificationFile) {
        String EducationCertificationAttachFileName = null;
        String EducationCertificationAttachFilePath = null;

        final ProfileEducation profileEducation =
                profileEducationQueryAdapter.getProfileEducation(profileEducationId);

        // 프로필 이력 인증서를 업데이트한다.
        if (fileValidator.validatingFileUpload(profileEducationCertificationFile)) {
            EducationCertificationAttachFileName =
                    Normalizer.normalize(
                            Objects.requireNonNull(
                                    profileEducationCertificationFile.getOriginalFilename()),
                            Form.NFC);
            EducationCertificationAttachFilePath =
                    s3Uploader.uploadProfileEducationFile(
                            new CertificationFile(profileEducationCertificationFile));
            profileEducation.setProfileEducationCertification(
                    true,
                    false,
                    EducationCertificationAttachFileName,
                    EducationCertificationAttachFilePath);
        }

        final LocalDateTime nowInSeoul = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        final ProfileEducationCertificationReportDto profileEducationCertificationReportDto =
                ProfileEducationCertificationReportDto.builder()
                        .profileEducationId(profileEducation.getId())
                        .emailId(profileEducation.getProfile().getMember().getEmailId())
                        .universityName(profileEducation.getUniversity().getUniversityName())
                        .educationCertificationAttachFileName(
                                profileEducation.getEducationCertificationAttachFileName())
                        .educationCertificationAttachFilePath(
                                profileEducation.getEducationCertificationAttachFilePath())
                        .uploadTime(nowInSeoul)
                        .build();

        discordProfileCertificationReportService.sendProfileEducationReport(
                profileEducationCertificationReportDto);

        return profileEducationMapper.toAddProfileEducationCertification(profileEducation);
    }

    public ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse
            removeProfileEducationCertification(
                    final Long memberId, final Long profileEducationId) {
        final ProfileEducation profileEducation =
                profileEducationQueryAdapter.getProfileEducation(profileEducationId);

        // 업로드 파일 삭제
        s3Uploader.deleteS3File(profileEducation.getEducationCertificationAttachFilePath());

        // 프로필 이력 인증 정보 업데이트
        profileEducation.setProfileEducationCertification(false, false, null, null);

        return profileEducationMapper.toRemoveProfileEducationCertification(profileEducationId);
    }
}
