package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Long validateProfileByMember(Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(INVALID_PROFILE_WITH_MEMBER);
        } else {
            return profileRepository.findByMemberId(memberId).getId();
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileDetail(final Long profileId){
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));
        return ProfileResponse.personalProfile(profile);
    }

    // 저장 로직 없이 업데이트만 필요함
    public void update(final Long profileId, final ProfileUpdateRequest profileUpdateRequest) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));
    }

    public void delete(final Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new BadRequestException(NOT_FOUND_PROFILE_ID);
        }
        profileRepository.deleteById(profileId);
    }
}
