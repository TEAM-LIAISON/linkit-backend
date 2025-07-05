package liaison.linkit.profile.implement.profile;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.exception.profile.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class ProfileQueryAdapter {

    private final ProfileRepository profileRepository;

    public Profile findById(final Long profileId) {
        return profileRepository
                .findById(profileId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByMemberId(final Long memberId) {
        return profileRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByEmailId(final String emailId) {
        return profileRepository
                .findByEmailId(emailId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition) {
        return profileRepository.findByMarketingConsentAndMajorPosition(majorPosition);
    }
}
