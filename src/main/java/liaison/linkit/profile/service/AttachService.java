package liaison.linkit.profile.service;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ATTACH_URL_WITH_PROFILE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ATTACH_URL_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

import java.util.List;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.attach.AttachUrl;
import liaison.linkit.profile.domain.repository.attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.dto.request.attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.attach.AttachUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttachService {
    private final ProfileRepository profileRepository;
    private final AttachUrlRepository attachUrlRepository;
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

    public void saveUrl(
            final Long memberId,
            final List<AttachUrlCreateRequest> attachUrlCreateRequests
    ) {
        final Profile profile = getProfile(memberId);

        // 기존에 존재 이력이 있다면
        if (attachUrlRepository.existsByProfileId(profile.getId())) {
            attachUrlRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsAttachUrl(false);
        }

        attachUrlCreateRequests.forEach(request -> {
            saveAttachUrl(profile, request);
        });

        profile.updateIsAttachUrl(true);
    }

    private void saveAttachUrl(final Profile profile, final AttachUrlCreateRequest attachUrlCreateRequest) {
        final AttachUrl newAttachUrl = AttachUrl.of(
                profile,
                attachUrlCreateRequest.getAttachUrlName(),
                attachUrlCreateRequest.getAttachUrlPath()
        );
        attachUrlRepository.save(newAttachUrl);
    }

    private AttachUrlResponse getAttachUrlResponse(final AttachUrl attachUrl) {
        return AttachUrlResponse.personalAttachUrl(attachUrl);
    }

    // 삭제 메서드
    public void deleteUrl(final Long memberId, final Long attachUrlId) {
        final Profile profile = getProfile(memberId);
        final AttachUrl attachUrl = getAttachUrl(attachUrlId);

        attachUrlRepository.deleteById(attachUrl.getId());

        if (!attachUrlRepository.existsByProfileId(profile.getId())) {
            profile.cancelPerfectionTen();
        }
    }

    public AttachResponse getAttachList(final Long memberId) {
        final Profile profile = getProfile(memberId);

        final List<AttachUrl> attachUrls = attachUrlRepository.findAllByProfileId(profile.getId());
        log.info("attachUrls={}", attachUrls);

        final List<AttachUrlResponse> attachUrlResponses = attachUrls.stream().map(this::getAttachUrlResponse).toList();
        log.info("attachUrlResponses={}", attachUrlResponses);

        return AttachResponse.getAttachResponse(attachUrlResponses);
    }

    @Transactional(readOnly = true)
    public AttachResponse getBrowseAttachList(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
        final List<AttachUrl> attachUrls = attachUrlRepository.findAllByProfileId(profile.getId());
        log.info("attachUrls={}", attachUrls);
        final List<AttachUrlResponse> attachUrlResponses = attachUrls.stream().map(this::getAttachUrlResponse).toList();
        log.info("attachUrlResponses={}", attachUrlResponses);

        return AttachResponse.getAttachResponse(attachUrlResponses);
    }
}
