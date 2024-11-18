package liaison.linkit.profile.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Objects;
import liaison.linkit.common.validator.FileValidator;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.ProfileAwardsMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsCommandAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsQueryAdapter;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.AddProfileAwardsResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.RemoveProfileAwardsResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.UpdateProfileAwardsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileAwardsService {
    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileAwardsQueryAdapter profileAwardsQueryAdapter;
    private final ProfileAwardsCommandAdapter profileAwardsCommandAdapter;
    private final ProfileAwardsMapper profileAwardsMapper;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ProfileAwardsResponseDTO.ProfileAwardsItems getProfileAwardsItems(final Long memberId) {
        log.info("memberId = {}의 내 수상 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileAwards> profileAwards = profileAwardsQueryAdapter.getProfileAwardsGroup(memberId);
        log.info("profileAwards = {}가 성공적으로 조회되었습니다.", profileAwards);

        return profileAwardsMapper.toProfileAwardsItems(profileAwards);
    }

    @Transactional(readOnly = true)
    public ProfileAwardsResponseDTO.ProfileAwardsDetail getProfileAwardsDetail(final Long memberId, final Long profileAwardsId) {
        log.info("memberId = {}의 내 수상 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfileAwards profileAwards = profileAwardsQueryAdapter.getProfileAwards(profileAwardsId);
        log.info("profileAwards = {}가 성공적으로 조회되었습니다.", profileAwards);

        return profileAwardsMapper.toProfileAwardsDetail(profileAwards);
    }

    public AddProfileAwardsResponse addProfileAwards(final Long memberId, final ProfileAwardsRequestDTO.AddProfileAwardsRequest request) {
        log.info("memberId = {}의 프로필 수상 추가 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfileAwards profileAwards = profileAwardsMapper.toAddProfileAwards(profile, request);
        final ProfileAwards savedProfileAwards = profileAwardsCommandAdapter.addProfileAwards(profileAwards);

        return profileAwardsMapper.toAddProfileAwardsResponse(savedProfileAwards);
    }

    public UpdateProfileAwardsResponse updateProfileAwards(final Long memberId, final Long profileAwardsId, final UpdateProfileAwardsRequest updateProfileAwardsRequest) {
        log.info("memberId = {}의 프로필 수상 수정 요청이 서비스 계층에 발생했습니다.", memberId);
        final ProfileAwards updatedProfileAwards = profileAwardsCommandAdapter.updateProfileAwards(profileAwardsId, updateProfileAwardsRequest);
        return profileAwardsMapper.toUpdateProfileAwardsResponse(updatedProfileAwards);
    }

    public RemoveProfileAwardsResponse removeProfileAwards(final Long memberId, final Long profileAwardsId) {
        final ProfileAwards profileAwards = profileAwardsQueryAdapter.getProfileAwards(profileAwardsId);

        if (profileAwards.getAwardsCertificationAttachFilePath() != null) {
            s3Uploader.deleteFile(profileAwards.getAwardsCertificationAttachFilePath());
            profileAwards.setProfileAwardsCertification(false, false, null, null);
        }

        profileAwardsCommandAdapter.removeProfileAwards(profileAwards);
        return profileAwardsMapper.toRemoveProfileAwards(profileAwardsId);
    }

    public ProfileAwardsResponseDTO.ProfileAwardsCertificationResponse addProfileAwardsCertification(
            final Long memberId,
            final Long profileAwardsId,
            final MultipartFile profileAwardsCertificationFile
    ) {
        String awardsCertificationAttachFileName = null;
        String awardsCertificationAttachFilePath = null;

        final ProfileAwards profileAwards = profileAwardsQueryAdapter.getProfileAwards(profileAwardsId);

        // 프로필 이력 인증서를 업데이트한다.
        if (fileValidator.validatingFileUpload(profileAwardsCertificationFile)) {
            awardsCertificationAttachFileName = Normalizer.normalize(Objects.requireNonNull(profileAwardsCertificationFile.getOriginalFilename()), Form.NFC);
            awardsCertificationAttachFilePath = s3Uploader.uploadProfileAwardsFile(new CertificationFile(profileAwardsCertificationFile));
            profileAwards.setProfileAwardsCertification(true, false, awardsCertificationAttachFileName, awardsCertificationAttachFilePath);
        }

        return profileAwardsMapper.toAddProfileAwardsCertification(profileAwards);
    }

    public ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse removeProfileAwardsCertification(
            final Long memberId,
            final Long profileAwardsId
    ) {
        final ProfileAwards profileAwards = profileAwardsQueryAdapter.getProfileAwards(profileAwardsId);

        // 업로드 파일 삭제
        s3Uploader.deleteFile(profileAwards.getAwardsCertificationAttachFilePath());

        // 프로필 이력 인증 정보 업데이트
        profileAwards.setProfileAwardsCertification(false, false, null, null);

        return profileAwardsMapper.toRemoveProfileAwardsCertification(profileAwardsId);
    }
}
