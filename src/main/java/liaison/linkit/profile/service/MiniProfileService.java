package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.S3ImageEvent;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.profile.domain.MiniProfile;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.MiniProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MiniProfileService {

    private final MiniProfileRepository miniProfileRepository;
    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    public Long validateMiniProfileByMember(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!miniProfileRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        } else {
            return miniProfileRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(final Long memberId,
                     final MiniProfileCreateRequest miniProfileCreateRequest,
                     final MultipartFile miniProfileImage
    ) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        // 전달받은 multipartFile을 파일 경로에 맞게 전달하는 작업이 필요함 (save)
        final String miniProfileImageUrl = saveImage(miniProfileImage);

        final MiniProfile newMiniProfile = MiniProfile.of(
                profile,
                miniProfileCreateRequest.getProfileTitle(),
                miniProfileCreateRequest.getUploadPeriod(),
                miniProfileCreateRequest.isUploadDeadline(),
                miniProfileImageUrl,
                miniProfileCreateRequest.getMyValue(),
                miniProfileCreateRequest.getSkillSets()
        );

        miniProfileRepository.save(newMiniProfile);
    }

    private MiniProfileResponse getMiniProfileResponse(final MiniProfile miniProfile) {
        return MiniProfileResponse.of(miniProfile);
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfileDetail(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));
        return MiniProfileResponse.personalMiniProfile(miniProfile);
    }

    public void update(final Long miniProfileId, final MiniProfileUpdateRequest miniProfileUpdateRequest) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(()-> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));

        miniProfile.update(miniProfileUpdateRequest);
        miniProfileRepository.save(miniProfile);
    }

    public void delete(final Long miniProfileId){
        if(!miniProfileRepository.existsById(miniProfileId)){
            throw new BadRequestException(NOT_FOUND_MINI_PROFILE_ID);
        }
        miniProfileRepository.deleteById(miniProfileId);
    }

    public String saveImage(final MultipartFile miniProfileImage) {
        // 이미지 유효성 검증
        validateSizeOfImage(miniProfileImage);
        // 이미지 파일 객체 생성 (file, HashedName)
        final ImageFile imageFile = new ImageFile(miniProfileImage);
        // 파일 이름을 반환한다? No! -> 파일 경로를 반환해야함.
        return uploadMiniProfileImage(imageFile);
    }

    private String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        try {
            return s3Uploader.uploadMiniProfileImage(miniProfileImageFile);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(miniProfileImageFile.getHashedName()));
            throw e;
        }
    }

    private void validateSizeOfImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }
}
