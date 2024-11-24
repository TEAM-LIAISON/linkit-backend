package liaison.linkit.profile.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Objects;
import liaison.linkit.common.validator.FileValidator;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.ProfileLicenseMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseCommandAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseQueryAdapter;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.UpdateProfileLicenseRequest;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.AddProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.RemoveProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.UpdateProfileLicenseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileLicenseService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;
    private final ProfileLicenseCommandAdapter profileLicenseCommandAdapter;
    private final ProfileLicenseMapper profileLicenseMapper;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ProfileLicenseResponseDTO.ProfileLicenseItems getProfileLicenseItems(final Long memberId) {
        log.info("memberId = {}의 내 자격증 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileLicense> profileLicenses = profileLicenseQueryAdapter.getProfileLicenses(memberId);
        log.info("profileLicenses = {}가 성공적으로 조회되었습니다.", profileLicenses);

        return profileLicenseMapper.toProfileLicenseItems(profileLicenses);
    }

    @Transactional(readOnly = true)
    public ProfileLicenseResponseDTO.ProfileLicenseDetail getProfileLicenseDetail(final Long memberId, final Long profileLicenseId) {
        log.info("memberId = {}의 내 자격증 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfileLicense profileLicense = profileLicenseQueryAdapter.getProfileLicense(profileLicenseId);
        log.info("profileLicense = {}가 성공적으로 조회되었습니다.", profileLicense);

        return profileLicenseMapper.toProfileLicenseDetail(profileLicense);
    }

    public AddProfileLicenseResponse addProfileLicense(final Long memberId, final ProfileLicenseRequestDTO.AddProfileLicenseRequest request) {
        log.info("memberId = {}의 프로필 자격증 추가 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileLicense profileLicense = profileLicenseMapper.toAddProfileLicense(profile, request);
        final ProfileLicense savedProfileLicense = profileLicenseCommandAdapter.addProfileLicense(profileLicense);

        // 만약 존재하지 않았다가 생긴 경우라면 true 변환 필요
        if (!profile.isProfileLicense()) {
            profile.setIsProfileLicense(true);
        }

        return profileLicenseMapper.toAddProfileLicenseResponse(savedProfileLicense);
    }

    public UpdateProfileLicenseResponse updateProfileLicense(final Long memberId, final Long profileLicenseId, final UpdateProfileLicenseRequest updateProfileLicenseRequest) {
        log.info("memberId = {}의 프로필 자격증 수정 요청이 서비스 계층에 발생했습니다.", memberId);
        final ProfileLicense updatedProfileLicense = profileLicenseCommandAdapter.updateProfileLicense(profileLicenseId, updateProfileLicenseRequest);
        return profileLicenseMapper.toUpdateProfileLicenseResponse(updatedProfileLicense);
    }

    public RemoveProfileLicenseResponse removeProfileLicense(final Long memberId, final Long profileLicenseId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final ProfileLicense profileLicense = profileLicenseQueryAdapter.getProfileLicense(profileLicenseId);

        if (profileLicense.getLicenseCertificationAttachFilePath() != null) {
            s3Uploader.deleteFile(profileLicense.getLicenseCertificationAttachFilePath());
            profileLicense.setProfileLicenseCertification(false, false, null, null);
        }

        profileLicenseCommandAdapter.removeProfileLicense(profileLicense);

        if (!profileLicenseQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfileLicense(false);
        }

        return profileLicenseMapper.toRemoveProfileLicense(profileLicenseId);
    }

    public ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse addProfileLicenseCertification(
            final Long memberId,
            final Long profileLicenseId,
            final MultipartFile profileLicenseCertificationFile
    ) {
        String LicenseCertificationAttachFileName = null;
        String LicenseCertificationAttachFilePath = null;

        final ProfileLicense profileLicense = profileLicenseQueryAdapter.getProfileLicense(profileLicenseId);

        // 프로필 자격증 인증서를 업데이트한다.
        if (fileValidator.validatingFileUpload(profileLicenseCertificationFile)) {
            LicenseCertificationAttachFileName = Normalizer.normalize(Objects.requireNonNull(profileLicenseCertificationFile.getOriginalFilename()), Form.NFC);
            LicenseCertificationAttachFilePath = s3Uploader.uploadProfileLicenseFile(new CertificationFile(profileLicenseCertificationFile));
            profileLicense.setProfileLicenseCertification(true, false, LicenseCertificationAttachFileName, LicenseCertificationAttachFilePath);
        }

        return profileLicenseMapper.toAddProfileLicenseCertification(profileLicense);
    }

    public ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse removeProfileLicenseCertification(
            final Long memberId,
            final Long profileLicenseId
    ) {
        final ProfileLicense profileLicense = profileLicenseQueryAdapter.getProfileLicense(profileLicenseId);

        // 업로드 파일 삭제
        s3Uploader.deleteFile(profileLicense.getLicenseCertificationAttachFilePath());

        // 프로필 자격증 인증 정보 업데이트
        profileLicense.setProfileLicenseCertification(false, false, null, null);

        return profileLicenseMapper.toRemoveProfileLicenseCertification(profileLicenseId);
    }

}
