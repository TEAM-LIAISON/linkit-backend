package liaison.linkit.profile.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.repository.currentState.ProfileCurrentStateRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.exception.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileQueryAdapter {
    private final ProfileRepository profileRepository;
    private final ProfileCurrentStateRepository profileCurrentStateRepository;

    public Profile findById(final Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByMemberId(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public List<ProfileCurrentState> findProfileCurrentStatesByProfileId(final Long profileId) {
        return profileCurrentStateRepository.findProfileCurrentStatesByProfileId(profileId);
    }
}
