package liaison.linkit.profile.service;

import static liaison.linkit.profile.domain.type.ProfileLogType.GENERAL_LOG;

import java.util.List;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.ProfileLogMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogTypeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileLogService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;
    private final ProfileLogMapper profileLogMapper;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ProfileLogItems getProfileLogItems(final Long memberId) {
        log.info("memberId = {}의 내 로그 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileLog> profileLogs = profileLogQueryAdapter.getProfileLogs(memberId);
        log.info("profileLogs = {}가 성공적으로 조회되었습니다.", profileLogs);

        return profileLogMapper.toProfileLogItems(profileLogs);
    }

    @Transactional(readOnly = true)
    public ProfileLogItem getProfileLogItem(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 내 로그 DTO 조회 요청 발생했습니다.", memberId);

        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        log.info("profileLog = {}가 성공적으로 조회되었습니다.", profileLog);

        return profileLogMapper.toProfileLogItem(profileLog);
    }

    public AddProfileLogResponse addProfileLog(final Long memberId, final ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest) {
        log.info("memberId = {}의 프로필 로그 추가 요청 발생했습니다.", memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final ProfileLog profileLog = new ProfileLog(null, profile, addProfileLogRequest.getLogTitle(), addProfileLogRequest.getLogContent(), addProfileLogRequest.getIsLogPublic(), GENERAL_LOG);
        final ProfileLog savedProfileLog = profileLogCommandAdapter.addProfileLog(profileLog);

        return profileLogMapper.toAddProfileLogResponse(savedProfileLog);
    }

    public RemoveProfileLogResponse removeProfileLog(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 프로필 로그 삭제 요청 발생했습니다.", memberId);
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        profileLogCommandAdapter.remove(profileLog);
        return profileLogMapper.toRemoveProfileLog(memberId, profileLogId);
    }

    public UpdateProfileLogTypeResponse updateProfileLogType(final Long memberId, final Long profileLogId, final ProfileLogRequestDTO.UpdateProfileLogType updateProfileLogType) {
        log.info("memberId = {}의 프로필 로그 = {}에 대한 대표글 설정 수정 요청 발생했습니다.", memberId, profileLogId);
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        final ProfileLog updatedProfileLog = profileLogCommandAdapter.updateProfileLogType(profileLog, updateProfileLogType);
        return profileLogMapper.toUpdateProfileLogType(updatedProfileLog);
    }

    public AddProfileLogBodyImageResponse addProfileLogBodyImage(final Long memberId, final MultipartFile profileLogBodyImage) {
        String profileLogBodyImagePath = null;
        log.info("memberId = {}의 프로필 로그 본문에 대한 이미지 추가 요청이 발생했습니다.", memberId);

        // 버켓에만 저장함
        if (imageValidator.validatingImageUpload(profileLogBodyImage)) {
            profileLogBodyImagePath = s3Uploader.uploadProfileLogBodyImage(new ImageFile(profileLogBodyImage));
        }

        // DB에 저장안하면 발생하는 문제점?
        // 수정요청이 들어갈때? 이미지가 남는다?

        return profileLogMapper.toAddProfileLogBodyImageResponse(profileLogBodyImagePath);
    }
}
