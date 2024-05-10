package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Attach.AttachUrl;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.Attach.AttachFileRepository;
import liaison.linkit.profile.domain.repository.Attach.AttachUrlRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.Attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.request.Attach.AttachUrlUpdateRequest;
import liaison.linkit.profile.dto.response.Attach.AttachUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ATTACH_URL_WITH_PROFILE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ATTACH_URL_ID;

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

    public void save(final Long memberId, final AttachUrlCreateRequest attachUrlCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final AttachUrl newAttachUrl = AttachUrl.of(
                profile,
                attachUrlCreateRequest.getAttachUrl()
        );

        attachUrlRepository.save(newAttachUrl);

        profile.updateIsAttach(true);
        profile.updateMemberProfileTypeByCompletion();
    }

    public AttachUrlResponse getAttachUrlDetail(final Long attachUrlId) {
        final AttachUrl attachUrl = attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_ID));
        return AttachUrlResponse.personalAttachUrl(attachUrl);
    }

    public void update(final Long memberId, final AttachUrlUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachUrlId = validateAttachUrlByMember(memberId);

        final AttachUrl attachUrl = attachUrlRepository.findById(attachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ATTACH_URL_ID));

        attachUrl.update(updateRequest);
        attachUrlRepository.save(attachUrl);

        // 수정에 대해서는 값 변경 함수를 호출하지 않는다.
        profile.updateMemberProfileTypeByCompletion();
    }

    public void delete(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long attachUrlId = validateAttachUrlByMember(memberId);

        if (!attachUrlRepository.existsById(attachUrlId)) {
            throw new BadRequestException(NOT_FOUND_ATTACH_URL_ID);
        }

        attachUrlRepository.deleteById(attachUrlId);

        profile.updateIsAttach(false);
        profile.updateMemberProfileTypeByCompletion();
    }
}
