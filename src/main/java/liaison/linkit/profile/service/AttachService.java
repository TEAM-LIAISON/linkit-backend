package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Attach.AttachFile;
import liaison.linkit.profile.domain.Attach.AttachUrl;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.Attach.AttachFileRepository;
import liaison.linkit.profile.domain.repository.Attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.Attach.AttachFileCreateRequest;
import liaison.linkit.profile.dto.request.Attach.AttachFileUpdateRequest;
import liaison.linkit.profile.dto.request.Attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.request.Attach.AttachUrlUpdateRequest;
import liaison.linkit.profile.dto.response.Attach.AttachFileResponse;
import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import liaison.linkit.profile.dto.response.Attach.AttachUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachService {

    private final ProfileRepository profileRepository;
    private final AttachUrlRepository attachUrlRepository;
    private final AttachFileRepository attachFileRepository;

    public Long validateAttachUrlByMember(final Long memberId) {
        final Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!attachUrlRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_ATTACH_URL_WITH_PROFILE);
        } else {
            return attachUrlRepository.findByProfileId(profileId).getId();
        }
    }

    public Long validateAttachFileByMember(final Long memberId) {
        final Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!attachFileRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_ATTACH_FILE_WITH_PROFILE);
        } else {
            return attachFileRepository.findByProfileId(profileId).getId();
        }
    }

    public void saveImage(final Long memberId, final AttachUrlCreateRequest attachUrlCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final AttachUrl newAttachUrl = AttachUrl.of(
                profile,
                attachUrlCreateRequest.getAttachUrl()
        );

        attachUrlRepository.save(newAttachUrl);

        // 프로필 상태 관리 첨부용으로 추가 필요
        // profile.updateMemberProfileTypeByCompletion();
    }

    public AttachUrlResponse getAttachUrlDetail(final Long attachUrlId) {
        final AttachUrl attachUrl = attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_ID));
        return getAttachUrlResponse(attachUrl);
    }

    private AttachUrlResponse getAttachUrlResponse(final AttachUrl attachUrl) {
        return AttachUrlResponse.personalAttachUrl(attachUrl);
    }

    public void updateImage(final Long memberId, final AttachUrlUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachUrlId = validateAttachUrlByMember(memberId);

        final AttachUrl attachUrl = attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_ID));

        attachUrl.update(updateRequest);
        attachUrlRepository.save(attachUrl);

        // 수정에 대해서는 값 변경 함수를 호출하지 않는다.
        profile.updateMemberProfileTypeByCompletion();
    }

    public void deleteImage(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachUrlId = validateAttachUrlByMember(memberId);

        if (!attachUrlRepository.existsById(attachUrlId)) {
            throw new BadRequestException(NOT_FOUND_ATTACH_URL_ID);
        }

        attachUrlRepository.deleteById(attachUrlId);
    }



    public void saveFile(final Long memberId, final AttachFileCreateRequest createRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final AttachFile newAttachFile = AttachFile.of(
                profile,
                createRequest.getAttachFile()
        );

        attachFileRepository.save(newAttachFile);

        // 프로필 상태 관리 첨부용으로 추가 필요
    }


    public AttachFileResponse getAttachFileDetail(final Long attachFileId) {
        final AttachFile attachFile = attachFileRepository.findById(attachFileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_FILE_ID));
        return getAttachFileResponse(attachFile);
    }

    private AttachFileResponse getAttachFileResponse(final AttachFile attachFile) {
        return AttachFileResponse.personalAttachFile(attachFile);
    }

    public void updateFile(final Long memberId, final AttachFileUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachFileId = validateAttachFileByMember(memberId);

        final AttachFile attachFile = attachFileRepository.findById(attachFileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_FILE_ID));

        attachFile.update(updateRequest);
        attachFileRepository.save(attachFile);
    }

    public void deleteFile(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachFileId = validateAttachFileByMember(memberId);

        if (!attachFileRepository.existsByProfileId(attachFileId)) {
            throw new BadRequestException(NOT_FOUND_ATTACH_FILE_ID);
        }

        attachFileRepository.deleteById(attachFileId);

        // 프로그레스바 상태 관련 함수 추가 필요
    }

    public AttachResponse getAttachList(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final List<AttachUrl> attachUrls = attachUrlRepository.findAllByProfileId(profileId);
        final List<AttachFile> attachFiles = attachFileRepository.findAllByProfileId(profileId);

        final List<AttachUrlResponse> attachUrlResponses = attachUrls.stream().map(this::getAttachUrlResponse).toList();
        final List<AttachFileResponse> attachFileResponses = attachFiles.stream().map(this::getAttachFileResponse).toList();

        return AttachResponse.getAttachResponse(attachUrlResponses, attachFileResponses);
    }

}
