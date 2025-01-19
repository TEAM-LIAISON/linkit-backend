package liaison.linkit.profile.business.service;

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
import liaison.linkit.profile.business.mapper.ProfileLogMapper;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogImage;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogImageCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogImageQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse;
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

        profileLog.increaseViewCount();

        return profileLogMapper.toProfileLogItem(profileLog);
    }

    // 프로필 로그 본문 이미지 추가
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

        return profileLogMapper.toAddProfileLogBodyImageResponse(savedImage);
    }

    // 프로필 로그 생성
    public AddProfileLogResponse addProfileLog(final Long memberId, final ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest) {
        log.info("memberId = {}의 프로필 로그 추가 요청 발생했습니다.", memberId);

        // 1. 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        LogType addProfileLogType = LogType.GENERAL_LOG;

        if (!profileLogQueryAdapter.existsRepresentativeProfileLogByProfile(profile.getId())) {
            addProfileLogType = LogType.REPRESENTATIVE_LOG;
        }

        // 2. ProfileLog 엔티티 생성 및 저장
        final ProfileLog profileLog = new ProfileLog(
                null,
                profile,
                addProfileLogRequest.getLogTitle(),
                addProfileLogRequest.getLogContent(),
                addProfileLogRequest.getIsLogPublic(),
                addProfileLogType,
                0L
        );

        final ProfileLog savedProfileLog = profileLogCommandAdapter.addProfileLog(profileLog);
        log.info("ProfileLog = {}가 성공적으로 저장되었습니다.", savedProfileLog);

        // 3. 글 내용에서 이미지 URL 추출
        final List<String> profileLogImagePaths = imageUtils.extractImageUrls(addProfileLogRequest.getLogContent());
        log.info("추출된 이미지 URL 목록: {}", profileLogImagePaths);

        if (!profileLogImagePaths.isEmpty()) {
            // 4. 이미지 URL로 Image 엔티티 조회
            final List<Image> images = imageQueryAdapter.findByImageUrls(profileLogImagePaths);
            log.info("조회된 Image 엔티티 목록: {}", images);

            // 5. ProfileLogImage 엔티티 생성 및 저장
            for (Image image : images) {
                // 5.1. ProfileLogImage 엔티티 생성
                ProfileLogImage profileLogImage = ProfileLogImage.builder()
                        .profileLog(savedProfileLog)
                        .image(image)
                        .build();

                // 5.2. ProfileLogImage 저장
                profileLogImageCommandAdapter.addProfileLogImage(profileLogImage);
                log.info("ProfileLogImage = {}가 성공적으로 저장되었습니다.", profileLogImage);

                // 6. 이미지의 isTemporary 필드 업데이트
                image.setTemporary(false);
            }
        } else {
            log.info("추출된 이미지 URL이 없습니다. 이미지 연계 작업을 생략합니다.");
        }

        return profileLogMapper.toAddProfileLogResponse(savedProfileLog);
    }

    // 프로필 로그 수정
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

    // 프로필 로그 삭제
    public RemoveProfileLogResponse removeProfileLog(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 프로필 로그 삭제 요청 발생했습니다.", memberId);

        // 1. ProfileLog 엔티티 조회
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        log.info("ProfileLog = {}가 성공적으로 조회되었습니다.", profileLog);

        // 2. ProfileLog와 연관된 ProfileLogImage 엔티티 조회
        List<ProfileLogImage> profileLogImages = profileLogImageQueryAdapter.findByProfileLog(profileLog);
        log.info("ProfileLog에 연관된 ProfileLogImage 개수: {}", profileLogImages.size());

        // 3. ProfileLogImage 삭제 및 Image 상태 업데이트
        for (ProfileLogImage profileLogImage : profileLogImages) {
            Image image = profileLogImage.getImage();
            // 3.1. ProfileLogImage 삭제
            profileLogImageCommandAdapter.removeProfileLogImage(profileLogImage);
            log.info("ProfileLogImage = {} 삭제 완료.", profileLogImage.getId());

            // 3.3. Image의 isTemporary 필드를 true로 업데이트
            image.setTemporary(true);
            imageCommandAdapter.addImage(image);
        }

        // 4. ProfileLog 삭제
        profileLogCommandAdapter.remove(profileLog);
        log.info("ProfileLog = {} 삭제 완료.", profileLogId);

        return profileLogMapper.toRemoveProfileLog(profileLogId);
    }

    // 프로필 로그 타입 수정
    public UpdateProfileLogTypeResponse updateProfileLogType(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 프로필 로그 = {}에 대한 대표글 설정 수정 요청 발생했습니다.", memberId, profileLogId);

        // 1. ProfileLog 엔티티 조회
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        log.info("ProfileLog = {}가 성공적으로 조회되었습니다.", profileLog);

        // 2. 현재 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 3. 기존 대표 로그 조회
        ProfileLog existingRepresentativeLog = null;
        if (profileLogQueryAdapter.existsRepresentativeProfileLogByProfile(profile.getId())) {
            existingRepresentativeLog = profileLogQueryAdapter.getRepresentativeProfileLog(profile.getId());
        }

        log.info("기존 대표 로그: {}", existingRepresentativeLog);

        // 4. 기존 대표 로그가 존재하고, 수정하려는 로그가 아닌 경우 기존 대표 로그를 일반 로그로 변경
        if (existingRepresentativeLog != null && !existingRepresentativeLog.getId().equals(profileLogId)) {
            log.info("기존 대표 로그가 존재하므로, 기존 대표 로그를 일반 로그로 변경합니다. 기존 대표 로그 ID: {}", existingRepresentativeLog.getId());

            profileLogCommandAdapter.updateProfileLogTypeRepresent(existingRepresentativeLog);
            log.info("기존 대표 로그(ID: {})가 일반 로그로 변경되었습니다.", existingRepresentativeLog.getId());
        }

        // 5. 수정하려는 로그를 대표 로그로 설정
        log.info("수정하려는 로그를 대표 로그로 설정합니다. 로그 ID: {}", profileLogId);
        profileLogCommandAdapter.updateProfileLogTypeRepresent(profileLog);

        return profileLogMapper.toUpdateProfileLogType(profileLog);
    }

    // 프로필 로그 공개 여부 수정
    public UpdateProfileLogPublicStateResponse updateProfileLogPublicState(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 프로필 로그 = {}에 대한 프로필 로그 공개 여부 수정 요청 발생했습니다.", memberId, profileLogId);

        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        final boolean isProfileLogCurrentPublicState = profileLog.isLogPublic();
        final ProfileLog updatedProfileLog = profileLogCommandAdapter.updateProfileLogPublicState(profileLog, isProfileLogCurrentPublicState);

        return profileLogMapper.toUpdateProfileLogPublicState(updatedProfileLog);
    }
}
