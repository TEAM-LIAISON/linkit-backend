package liaison.linkit.profile.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Objects;
import liaison.linkit.common.validator.FileValidator;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.ProfileActivityMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.activity.ProfileActivityCommandAdapter;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
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

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityItems getProfileActivityItems(final Long memberId) {
        log.info("memberId = {}의 내 이력 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileActivity> profileActivities = profileActivityQueryAdapter.getProfileActivities(memberId);
        log.info("profileActivities = {}가 성공적으로 조회되었습니다.", profileActivities);

        return profileActivityMapper.toProfileActivityItems(profileActivities);
    }

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityDetail getProfileActivityDetail(final Long memberId, final Long profileActivityId) {
        log.info("memberId = {}의 내 이력 Detail 조회 요청 발생했습니다.", memberId);

        final ProfileActivity profileActivity = profileActivityQueryAdapter.getProfileActivity(profileActivityId);
        log.info("profileActivity = {}가 성공적으로 조회되었습니다.", profileActivity);

        return profileActivityMapper.toProfileActivityDetail(profileActivity);
    }

    public ProfileActivityResponseDTO.ProfileActivityResponse addProfileActivity(final Long memberId, final ProfileActivityRequestDTO.AddProfileActivityRequest request) {
        log.info("memberId = {}의 프로필 이력 추가 요청 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileActivity profileActivity = profileActivityMapper.toAddProfileActivity(profile, request);
        final ProfileActivity savedProfileActivity = profileActivityCommandAdapter.addProfileActivity(profileActivity);

        return profileActivityMapper.toAddProfileActivityResponse(savedProfileActivity);
    }

    public ProfileActivityResponseDTO.ProfileActivityCertificationResponse addProfileActivityCertification(
            final Long memberId,
            final Long profileActivityId,
            final MultipartFile profileActivityCertificationFile
    ) {
        String activityCertificationAttachFileName = null;
        String activityCertificationAttachFilePath = null;

        final ProfileActivity profileActivity = profileActivityQueryAdapter.getProfileActivity(profileActivityId);

        // 프로필 이력 인증서를 업데이트한다.
        if (fileValidator.validatingFileUpload(profileActivityCertificationFile)) {
            activityCertificationAttachFileName = Normalizer.normalize(Objects.requireNonNull(profileActivityCertificationFile.getOriginalFilename()), Form.NFC);
            activityCertificationAttachFilePath = s3Uploader.uploadProfileActivityFile(new CertificationFile(profileActivityCertificationFile));
            profileActivity.setProfileActivityCertification(true, false, activityCertificationAttachFileName, activityCertificationAttachFilePath);
        }

        return profileActivityMapper.toAddProfileActivityCertification(profileActivity);
    }
}
