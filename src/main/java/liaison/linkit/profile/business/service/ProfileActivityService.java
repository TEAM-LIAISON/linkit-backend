package liaison.linkit.profile.business.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Objects;

import liaison.linkit.common.validator.FileValidator;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.mapper.ProfileActivityMapper;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.activity.ProfileActivityCommandAdapter;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.UpdateProfileActivityRequest;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.AddProfileActivityResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.RemoveProfileActivityResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.UpdateProfileActivityResponse;
import liaison.linkit.report.certification.dto.profile.activity.ProfileActivityCertificationReportDto;
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
public class ProfileActivityService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfileActivityCommandAdapter profileActivityCommandAdapter;
    private final ProfileActivityMapper profileActivityMapper;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;
    private final DiscordProfileCertificationReportService discordProfileCertificationReportService;

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityItems getProfileActivityItems(
            final Long memberId) {
        log.info("memberId = {}의 내 이력 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileActivity> profileActivities =
                profileActivityQueryAdapter.getProfileActivities(memberId);
        log.info("profileActivities = {}가 성공적으로 조회되었습니다.", profileActivities);

        return profileActivityMapper.toProfileActivityItems(profileActivities);
    }

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityDetail getProfileActivityDetail(
            final Long memberId, final Long profileActivityId) {
        log.info("memberId = {}의 내 이력 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfileActivity profileActivity =
                profileActivityQueryAdapter.getProfileActivity(profileActivityId);
        log.info("profileActivity = {}가 성공적으로 조회되었습니다.", profileActivity);

        return profileActivityMapper.toProfileActivityDetail(profileActivity);
    }

    public AddProfileActivityResponse addProfileActivity(
            final Long memberId,
            final ProfileActivityRequestDTO.AddProfileActivityRequest request) {
        log.info("memberId = {}의 프로필 이력 추가 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileActivity profileActivity =
                profileActivityMapper.toAddProfileActivity(profile, request);
        final ProfileActivity savedProfileActivity =
                profileActivityCommandAdapter.addProfileActivity(profileActivity);

        if (!profile.isProfileActivity()) {
            profile.setIsProfileActivity(true);
            profile.addProfileActivityCompletion();
        }

        return profileActivityMapper.toAddProfileActivityResponse(savedProfileActivity);
    }

    public UpdateProfileActivityResponse updateProfileActivity(
            final Long memberId,
            final Long profileActivityId,
            final UpdateProfileActivityRequest updateProfileActivityRequest) {
        log.info("memberId = {}의 프로필 이력 수정 요청이 서비스 계층에 발생했습니다.", memberId);
        final ProfileActivity updatedProfileActivity =
                profileActivityCommandAdapter.updateProfileActivity(
                        profileActivityId, updateProfileActivityRequest);
        return profileActivityMapper.toUpdateProfileActivityResponse(updatedProfileActivity);
    }

    public RemoveProfileActivityResponse removeProfileActivity(
            final Long memberId, final Long profileActivityId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileActivity profileActivity =
                profileActivityQueryAdapter.getProfileActivity(profileActivityId);

        if (profileActivity.getActivityCertificationAttachFilePath() != null) {
            s3Uploader.deleteS3File(profileActivity.getActivityCertificationAttachFilePath());
            profileActivity.setProfileActivityCertification(false, false, null, null);
        }

        profileActivityCommandAdapter.removeProfileActivity(profileActivity);
        if (!profileActivityQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfileActivity(false);
            profile.removeProfileActivityCompletion();
        }

        return profileActivityMapper.toRemoveProfileActivity(profileActivityId);
    }

    public ProfileActivityResponseDTO.ProfileActivityCertificationResponse
            addProfileActivityCertification(
                    final Long memberId,
                    final Long profileActivityId,
                    final MultipartFile profileActivityCertificationFile) {
        String activityCertificationAttachFileName = null;
        String activityCertificationAttachFilePath = null;

        final ProfileActivity profileActivity =
                profileActivityQueryAdapter.getProfileActivity(profileActivityId);

        // 프로필 이력 인증서를 업데이트한다.
        if (fileValidator.validatingFileUpload(profileActivityCertificationFile)) {
            activityCertificationAttachFileName =
                    Normalizer.normalize(
                            Objects.requireNonNull(
                                    profileActivityCertificationFile.getOriginalFilename()),
                            Form.NFC);
            activityCertificationAttachFilePath =
                    s3Uploader.uploadProfileActivityFile(
                            new CertificationFile(profileActivityCertificationFile));
            profileActivity.setProfileActivityCertification(
                    true,
                    false,
                    activityCertificationAttachFileName,
                    activityCertificationAttachFilePath);
        }

        final ProfileActivityCertificationReportDto profileActivityCertificationReportDto =
                ProfileActivityCertificationReportDto.builder()
                        .profileActivityId(profileActivity.getId())
                        .emailId(profileActivity.getProfile().getMember().getEmailId())
                        .activityName(profileActivity.getActivityName())
                        .activityCertificationAttachFileName(
                                profileActivity.getActivityCertificationAttachFileName())
                        .activityCertificationAttachFilePath(
                                profileActivity.getActivityCertificationAttachFilePath())
                        .build();

        discordProfileCertificationReportService.sendProfileActivityReport(
                profileActivityCertificationReportDto);

        return profileActivityMapper.toAddProfileActivityCertification(profileActivity);
    }

    public ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse
            removeProfileActivityCertification(final Long memberId, final Long profileActivityId) {
        final ProfileActivity profileActivity =
                profileActivityQueryAdapter.getProfileActivity(profileActivityId);

        // 업로드 파일 삭제
        s3Uploader.deleteS3File(profileActivity.getActivityCertificationAttachFilePath());

        // 프로필 이력 인증 정보 업데이트
        profileActivity.setProfileActivityCertification(false, false, null, null);

        return profileActivityMapper.toRemoveProfileActivityCertification(profileActivityId);
    }
}
