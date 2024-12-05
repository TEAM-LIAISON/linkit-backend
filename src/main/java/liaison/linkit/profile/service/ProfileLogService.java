package liaison.linkit.profile.service;

import static liaison.linkit.profile.domain.type.LogType.GENERAL_LOG;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.implement.ImageCommandAdapter;
import liaison.linkit.image.implement.ImageQueryAdapter;
import liaison.linkit.image.util.ImageUtils;
import liaison.linkit.profile.business.ProfileLogMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.ProfileLogImage;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogImageCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogImageQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogResponse;
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

    private final ProfileLogImageQueryAdapter profileLogImageQueryAdapter;
    private final ProfileLogImageCommandAdapter profileLogImageCommandAdapter;

    private final ImageCommandAdapter imageCommandAdapter;
    private final ImageQueryAdapter imageQueryAdapter;

    private final ImageValidator imageValidator;
    private final ImageUtils imageUtils;

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

        // content에서 URL 추출해서 연결짓는 작업 필요
        final List<String> profileLogImagePaths = imageUtils.extractImageUrls(addProfileLogRequest.getLogContent());

        // 각 profileLogImagePath를 ProfileLog 테이블에 있는 content 부분이랑 찾아서 해당 PK를 ProfileLogImage

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

        // 버켓에 이미지 저장함
        if (imageValidator.validatingImageUpload(profileLogBodyImage)) {
            profileLogBodyImagePath = s3Uploader.uploadProfileLogBodyImage(new ImageFile(profileLogBodyImage));
        }

        // DB에 이미지 저장
        final Image image = new Image(null, profileLogBodyImagePath, true, LocalDateTime.now());
        final Image savedImage = imageCommandAdapter.addImage(image);

        return profileLogMapper.toAddProfileLogBodyImageResponse(profileLogBodyImagePath);
    }

    public UpdateProfileLogResponse updateProfileLog(final Long memberId, final Long profileLogId, final UpdateProfileLogRequest updateProfileLogRequest) {
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 프로필 로그 업데이트
        final ProfileLog updatedProfileLog = profileLogCommandAdapter.updateProfileLog(profileLog, updateProfileLogRequest);

        // 글 내용에서 이미지 URL 추출
        final List<String> profileLogImagePaths = imageUtils.extractImageUrls(updateProfileLogRequest.getLogContent());

        // 이미지 URL로 Image 엔티티 조회
        final List<Image> images = imageQueryAdapter.findByImageUrls(profileLogImagePaths);

        // 기존 ProfileLogImage 조회
        final List<ProfileLogImage> existingProfileLogImages = profileLogImageQueryAdapter.findByProfileLog(profileLog);

        // 현재 사용되는 이미지 ID
        Set<Long> currentImageIds = images.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        // 기존에 사용되던 이미지 ID
        Set<Long> existingImageIds = existingProfileLogImages.stream()
                .map(profileLogImage -> profileLogImage.getImage().getId())
                .collect(Collectors.toSet());

        // 새로운 이미지 ID
        Set<Long> newImageIds = new HashSet<>(currentImageIds);
        newImageIds.removeAll(existingImageIds);

        // 삭제된 이미지 ID
        Set<Long> removedImageIds = new HashSet<>(existingImageIds);
        removedImageIds.removeAll(currentImageIds);

        // 새로운 이미지에 대해 ProfileLogImage 생성 및 이미지 상태 업데이트
        for (Image image : images) {
            if (newImageIds.contains(image.getId())) {
                ProfileLogImage profileLogImage = ProfileLogImage.builder()
                        .profileLog(profileLog)
                        .image(image)
                        .build();
                profileLogImageCommandAdapter.addProfileLogImage(profileLogImage);

                // 이미지의 isTemporary를 false로 업데이트
                image.setTemporary(false);
                imageCommandAdapter.addImage(image);
            }
        }

        // 삭제된 이미지에 대한 ProfileLogImage 관계 제거
        for (ProfileLogImage profileLogImage : existingProfileLogImages) {
            if (removedImageIds.contains(profileLogImage.getImage().getId())) {
                profileLogImageCommandAdapter.removeProfileLogImage(profileLogImage);
            }
        }

        return profileLogMapper.toUpdateProfileLogResponse(updatedProfileLog);
    }

}
