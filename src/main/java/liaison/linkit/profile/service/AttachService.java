package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.FileException;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.attach.AttachUrl;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.attach.AttachUrlRepository;
import liaison.linkit.profile.dto.request.attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.request.attach.AttachUrlUpdateRequest;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.attach.AttachUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttachService {

    private final ProfileRepository profileRepository;
    private final AttachUrlRepository attachUrlRepository;
//    private final AttachFileRepository attachFileRepository;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 단일 첨부 URL 조회
    private AttachUrl getAttachUrl(final Long attachUrlId) {
        return attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_BY_ID));
    }

    // 전체 첨부 URL 조회
    private List<AttachUrl> getAttachUrls(final Long profileId) {
        try {
            return attachUrlRepository.findAllByProfileId(profileId);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_ATTACH_URLS_BY_PROFILE_ID);
        }

    }

    // 해당 회원이 1개라도 Attach URL 보유하고 있는지
    public void validateAttachUrlByMember(final Long memberId) {
        if (!attachUrlRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(INVALID_ATTACH_URL_WITH_PROFILE);
        }
    }

    public void validateAttachUrlByProfile(final Long profileId) {
        if (!attachUrlRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_ATTACH_URL_WITH_PROFILE);
        }
    }

    // 단일 첨부 File 조회
//    private AttachFile getAttachFile(final Long attachFileId) {
//        return attachFileRepository.findById(attachFileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_FILE_BY_ID));
//    }
//
    // 전체 첨부 File 조회
//    private List<AttachFile> getAttachFiles(final Long profileId) {
//        try {
//            return attachFileRepository.findAllByProfileId(profileId);
//        } catch (Exception e) {
//            throw new BadRequestException(NOT_FOUND_ATTACH_URLS_BY_PROFILE_ID);
//        }
//    }

    // 해당 회원이 1개라도 Attach File 보유하고 있는지
//    public void validateAttachFileByMember(final Long memberId) {
//        if (!attachFileRepository.existsByProfileId(getProfile(memberId).getId())) {
//            throw new AuthException(INVALID_ATTACH_FILE_WITH_PROFILE);
//        }
//    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    public void saveUrl(
            final Long memberId,
            final List<AttachUrlCreateRequest> attachUrlCreateRequests
    ) {
        final Profile profile = getProfile(memberId);

        // 기존에 존재 이력이 있다면
        if (attachUrlRepository.existsByProfileId(profile.getId())) {
            attachUrlRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsAttachUrl(false);
            profile.updateMemberProfileTypeByCompletion();
        }

        attachUrlCreateRequests.forEach(request -> {
            saveAttachUrl(profile, request);
        });

        profile.updateIsAttachUrl(true);
        profile.updateMemberProfileTypeByCompletion();
    }

    private void saveAttachUrl(final Profile profile, final AttachUrlCreateRequest attachUrlCreateRequest) {
        final AttachUrl newAttachUrl = AttachUrl.of(
                profile,
                attachUrlCreateRequest.getAttachUrlName(),
                attachUrlCreateRequest.getAttachUrlPath()
        );
        attachUrlRepository.save(newAttachUrl);
    }

    public AttachUrlResponse getAttachUrlDetail(final Long attachUrlId) {
        final AttachUrl attachUrl = attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_ID));
        return getAttachUrlResponse(attachUrl);
    }

    private AttachUrlResponse getAttachUrlResponse(final AttachUrl attachUrl) {
        return AttachUrlResponse.personalAttachUrl(attachUrl);
    }

    // 수정 메서드
    // 특정 URL 수정
    public void updateUrl(final Long attachUrlId, final AttachUrlUpdateRequest updateRequest) {
        final AttachUrl attachUrl = getAttachUrl(attachUrlId);
        attachUrl.update(updateRequest);
    }

    public void deleteAllUrl(final Long memberId) {
        final Profile profile = getProfile(memberId);
        if (attachUrlRepository.existsByProfileId(profile.getId())) {
            attachUrlRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsAttachUrl(false);
            profile.updateMemberProfileTypeByCompletion();
        }
    }

    // 삭제 메서드
    public void deleteUrl(final Long memberId, final Long attachUrlId) {
        final Profile profile = getProfile(memberId);
        final AttachUrl attachUrl = getAttachUrl(attachUrlId);

        attachUrlRepository.deleteById(attachUrl.getId());

        if (!attachUrlRepository.existsByProfileId(profile.getId())) {
            profile.cancelPerfectionTen();
            profile.updateMemberProfileTypeByCompletion();
        }
    }

//    public void saveFile(
//            final Long memberId,
//            final MultipartFile attachFile
//    ) {
//        final Profile profile = getProfile(memberId);
//        final String attachFileUrl = saveFileS3(attachFile);
//        final AttachFile newAttachFile = AttachFile.of(
//                profile,
//                attachFile.getOriginalFilename(),
//                attachFileUrl
//        );
//
//        attachFileRepository.save(newAttachFile);
//        profile.updateIsAttachFile(true);
//        // 프로필 상태 관리 첨부용으로 추가 필요
//    }
//
//    private String saveFileS3(final MultipartFile attachFile) {
//        validateSizeofFile(attachFile);
//        final PortfolioFile portfolioFile = new PortfolioFile(attachFile);
//        return uploadPortfolioFile(portfolioFile);
//    }

//    private String uploadPortfolioFile(final PortfolioFile portfolioFile) {
//        try {
//            return s3Uploader.uploadPortfolioFile(portfolioFile);
//        } catch (final Exception e) {
//            publisher.publishEvent(new S3PortfolioEvent(portfolioFile.getHashedName()));
//            throw e;
//        }
//    }

    private void validateSizeofFile(final MultipartFile attachFile) {
        if (attachFile == null || attachFile.isEmpty()) {
            throw new FileException(EMPTY_ATTACH_FILE);
        }
    }

    // 조회 메서드
//    public AttachFileResponse getAttachFileDetail(final Long attachFileId) {
//        final AttachFile attachFile = attachFileRepository.findById(attachFileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_FILE_ID));
//        return getAttachFileResponse(attachFile);
//    }

//    private AttachFileResponse getAttachFileResponse(final AttachFile attachFile) {
//        return AttachFileResponse.personalAttachFile(attachFile);
//    }

//    // 수정 메서드
//    public void updateFile(final Long memberId, final AttachFileUpdateRequest updateRequest) {
//        final Profile profile = getProfile(memberId);
//        final Long attachFileId = validateAttachFileByMember(memberId);
//
//        final AttachFile attachFile = attachFileRepository.findById(attachFileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_FILE_ID));
//
//        attachFile.update(updateRequest);
//        attachFileRepository.save(attachFile);
//    }
//
//    public void deleteFile(final Long memberId) {
//        final Profile profile = profileRepository.findByMemberId(memberId);
//        final Long attachFileId = validateAttachFileByMember(memberId);
//
//        if (!attachFileRepository.existsByProfileId(attachFileId)) {
//            throw new BadRequestException(NOT_FOUND_ATTACH_FILE_ID);
//        }
//
//        attachFileRepository.deleteById(attachFileId);
//
//        // 프로그레스바 상태 관련 함수 추가 필요
//    }

    public AttachResponse getAttachList(final Long memberId) {
        final Profile profile = getProfile(memberId);

        final List<AttachUrl> attachUrls = attachUrlRepository.findAllByProfileId(profile.getId());
        log.info("attachUrls={}", attachUrls);

//        final List<AttachFile> attachFiles = attachFileRepository.findAllByProfileId(profile.getId());
//        log.info("attachFiles={}", attachFiles);

        final List<AttachUrlResponse> attachUrlResponses = attachUrls.stream().map(this::getAttachUrlResponse).toList();
        log.info("attachUrlResponses={}", attachUrlResponses);

//        final List<AttachFileResponse> attachFileResponses = attachFiles.stream().map(this::getAttachFileResponse).toList();
//        log.info("attachFileResponses={}", attachFileResponses);

        return AttachResponse.getAttachResponse(attachUrlResponses);
    }
    @Transactional(readOnly = true)
    public AttachResponse getBrowseAttachList(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

        final List<AttachUrl> attachUrls = attachUrlRepository.findAllByProfileId(profile.getId());
        log.info("attachUrls={}", attachUrls);

//        final List<AttachFile> attachFiles = attachFileRepository.findAllByProfileId(profile.getId());
//        log.info("attachFiles={}", attachFiles);

        final List<AttachUrlResponse> attachUrlResponses = attachUrls.stream().map(this::getAttachUrlResponse).toList();
        log.info("attachUrlResponses={}", attachUrlResponses);

//        final List<AttachFileResponse> attachFileResponses = attachFiles.stream().map(this::getAttachFileResponse).toList();
//        log.info("attachFileResponses={}", attachFileResponses);

        return AttachResponse.getAttachResponse(attachUrlResponses);
    }




//    public void deleteFile(
//            final Long memberId,
//            final Long attachFileUrlId
//    ) {
//        final Profile profile = getProfile(memberId);
//        attachFileRepository.deleteById(attachFileUrlId);
//        if (!attachFileRepository.existsByProfileId(profile.getId()) && !attachUrlRepository.existsByProfileId(profile.getId())) {
//            profile.cancelPerfectionTen();
//            profile.updateMemberProfileTypeByCompletion();
//        }
//    }
}
