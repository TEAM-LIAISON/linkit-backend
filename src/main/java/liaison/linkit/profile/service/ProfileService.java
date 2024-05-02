package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;

    public Long validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        } else {
            return profileRepository.findByMemberId(memberId).getId();
        }
    }

    public void update(final Long profileId, final ProfileUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        profile.update(updateRequest);
        profileRepository.save(profile);
    }
}
