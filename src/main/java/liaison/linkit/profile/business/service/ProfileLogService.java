package liaison.linkit.profile.business.service;

import java.time.LocalDate;
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
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfileLogMapper;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogImage;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.profile.exception.log.UpdateProfileLogTypeBadRequestException;
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
import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import liaison.linkit.visit.dailyviewcount.domain.LogViewType;
import liaison.linkit.visit.dailyviewcount.repository.LogDailyViewCountRepository;
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
    private final MemberQueryAdapter memberQueryAdapter;
    private final LogDailyViewCountRepository logDailyViewCountRepository;

    // 로그 전체 조회
    @Transactional(readOnly = true)
    public ProfileLogItems getProfileLogItems(final Long memberId) {

        final List<ProfileLog> profileLogs = profileLogQueryAdapter.getProfileLogs(memberId);

        return profileLogMapper.toProfileLogItems(profileLogs);
    }

    // 로그 뷰어 전체 조회
    @Transactional(readOnly = true)
    public ProfileLogItems getProfileLogViewItems(final String emailId) {
        final Member member = memberQueryAdapter.findByEmailId(emailId);
        final List<ProfileLog> profileLogs =
                profileLogQueryAdapter.getProfileLogsPublic(member.getId());
        return profileLogMapper.toProfileLogItems(profileLogs);
    }

    @Transactional
    public ProfileLogItem getProfileLogItem(final Long memberId, final Long profileLogId) {

        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        profileLog.increaseViewCount();

        LocalDate today = LocalDate.now();
        LogDailyViewCount dailyViewCount =
                logDailyViewCountRepository
                        .findByLogViewTypeAndLogIdAndDate(
                                LogViewType.PROFILE_LOG, profileLogId, today)
                        .orElseGet(
                                () -> {
                                    // 해당 일자의 기록이 없으면 새로 생성
                                    LogDailyViewCount newCount =
                                            LogDailyViewCount.builder()
                                                    .logViewType(LogViewType.PROFILE_LOG)
                                                    .logId(profileLogId)
                                                    .date(today)
                                                    .dailyViewCount(
                                                            0L) // 초기값은 0, increase() 메서드에서 증가시킬 것
                                                    .build();
                                    return logDailyViewCountRepository.save(newCount);
                                });

        // 일별 조회수 증가
        dailyViewCount.increase();

        return profileLogMapper.toProfileLogItem(profileLog);
    }

    @Transactional
    public ProfileLogItem getProfileLogViewItem(final Long profileLogId) {
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        profileLog.increaseViewCount();
        return profileLogMapper.toProfileLogItem(profileLog);
    }

    // 프로필 로그 본문 이미지 추가
    public AddProfileLogBodyImageResponse addProfileLogBodyImage(
            final Long memberId, final MultipartFile profileLogBodyImage) {
        String profileLogBodyImagePath = null;

        // 버켓에 이미지 저장함
        if (imageValidator.validatingImageUpload(profileLogBodyImage)) {
            profileLogBodyImagePath =
                    s3Uploader.uploadProfileLogBodyImage(new ImageFile(profileLogBodyImage));
        }

        // DB에 이미지 저장
        final Image image = new Image(null, profileLogBodyImagePath, true, LocalDateTime.now());
        final Image savedImage = imageCommandAdapter.addImage(image);

        return profileLogMapper.toAddProfileLogBodyImageResponse(savedImage);
    }

    // 프로필 로그 생성
    public AddProfileLogResponse addProfileLog(
            final Long memberId,
            final ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest) {

        // 1. 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        LogType addProfileLogType = LogType.GENERAL_LOG;

        if (!profileLogQueryAdapter.existsRepresentativeProfileLogByProfile(profile.getId())) {
            addProfileLogType = LogType.REPRESENTATIVE_LOG;
        }

        // 2. ProfileLog 엔티티 생성 및 저장
        final ProfileLog profileLog =
                new ProfileLog(
                        null,
                        profile,
                        addProfileLogRequest.getLogTitle(),
                        addProfileLogRequest.getLogContent(),
                        addProfileLogRequest.getIsLogPublic(),
                        addProfileLogType,
                        0L, // viewCount 초기값
                        0L); // commentCount 초기값

        final ProfileLog savedProfileLog = profileLogCommandAdapter.addProfileLog(profileLog);

        // 3. 글 내용에서 이미지 URL 추출
        final List<String> profileLogImagePaths =
                imageUtils.extractImageUrls(addProfileLogRequest.getLogContent());

        if (!profileLogImagePaths.isEmpty()) {
            // 4. 이미지 URL로 Image 엔티티 조회
            final List<Image> images = imageQueryAdapter.findByImageUrls(profileLogImagePaths);

            // 5. ProfileLogImage 엔티티 생성 및 저장
            for (Image image : images) {
                // 5.1. ProfileLogImage 엔티티 생성
                ProfileLogImage profileLogImage =
                        ProfileLogImage.builder().profileLog(savedProfileLog).image(image).build();

                // 5.2. ProfileLogImage 저장
                profileLogImageCommandAdapter.addProfileLogImage(profileLogImage);

                // 6. 이미지의 isTemporary 필드 업데이트
                image.setTemporary(false);
            }
        }
        return profileLogMapper.toAddProfileLogResponse(savedProfileLog);
    }

    // 프로필 로그 수정
    public UpdateProfileLogResponse updateProfileLog(
            final Long memberId,
            final Long profileLogId,
            final UpdateProfileLogRequest updateProfileLogRequest) {
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 프로필 로그 업데이트
        final ProfileLog updatedProfileLog =
                profileLogCommandAdapter.updateProfileLog(profileLog, updateProfileLogRequest);

        // 글 내용에서 이미지 URL 추출
        final List<String> profileLogImagePaths =
                imageUtils.extractImageUrls(updateProfileLogRequest.getLogContent());

        // 이미지 URL로 Image 엔티티 조회
        final List<Image> images = imageQueryAdapter.findByImageUrls(profileLogImagePaths);

        // 기존 ProfileLogImage 조회
        final List<ProfileLogImage> existingProfileLogImages =
                profileLogImageQueryAdapter.findByProfileLog(profileLog);

        // 현재 사용되는 이미지 ID
        Set<Long> currentImageIds = images.stream().map(Image::getId).collect(Collectors.toSet());

        // 기존에 사용되던 이미지 ID
        Set<Long> existingImageIds =
                existingProfileLogImages.stream()
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
                ProfileLogImage profileLogImage =
                        ProfileLogImage.builder().profileLog(profileLog).image(image).build();
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

        // 1. ProfileLog 엔티티 조회
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 2. ProfileLog와 연관된 ProfileLogImage 엔티티 조회
        List<ProfileLogImage> profileLogImages =
                profileLogImageQueryAdapter.findByProfileLog(profileLog);

        // 3. ProfileLogImage 삭제 및 Image 상태 업데이트
        for (ProfileLogImage profileLogImage : profileLogImages) {
            Image image = profileLogImage.getImage();
            // 3.1. ProfileLogImage 삭제
            profileLogImageCommandAdapter.removeProfileLogImage(profileLogImage);

            // 3.3. Image의 isTemporary 필드를 true로 업데이트
            image.setTemporary(true);
            imageCommandAdapter.addImage(image);
        }

        // 4. ProfileLog 삭제
        profileLogCommandAdapter.remove(profileLog);

        return profileLogMapper.toRemoveProfileLog(profileLogId);
    }

    // 프로필 로그 타입 수정
    public UpdateProfileLogTypeResponse updateProfileLogType(
            final Long memberId, final Long profileLogId) {

        // 1. ProfileLog 엔티티 조회
        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        if (!profileLog.isLogPublic()) {
            throw UpdateProfileLogTypeBadRequestException.EXCEPTION;
        }

        // 2. 현재 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 3. 기존 대표 로그 조회
        ProfileLog existingRepresentativeLog = null;
        if (profileLogQueryAdapter.existsRepresentativeProfileLogByProfile(profile.getId())) {
            existingRepresentativeLog =
                    profileLogQueryAdapter.getRepresentativeProfileLog(profile.getId());
        }

        // 4. 기존 대표 로그가 존재하고, 수정하려는 로그가 아닌 경우 기존 대표 로그를 일반 로그로 변경
        if (existingRepresentativeLog != null
                && !existingRepresentativeLog.getId().equals(profileLogId)) {

            profileLogCommandAdapter.updateProfileLogTypeGeneral(existingRepresentativeLog);
        }

        // 6) 수정 대상 로그 -> 대표 로그로 변경
        log.debug("로그(ID={})를 대표 로그로 설정", profileLogId);
        profileLogCommandAdapter.updateProfileLogTypeRepresent(profileLog);

        return profileLogMapper.toUpdateProfileLogType(profileLog);
    }

    // 프로필 로그 공개 여부 수정
    public UpdateProfileLogPublicStateResponse updateProfileLogPublicState(
            final Long memberId, final Long profileLogId) {

        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        final boolean isProfileLogCurrentPublicState = profileLog.isLogPublic();
        final ProfileLog updatedProfileLog =
                profileLogCommandAdapter.updateProfileLogPublicState(
                        profileLog, isProfileLogCurrentPublicState);

        return profileLogMapper.toUpdateProfileLogPublicState(updatedProfileLog);
    }
}
