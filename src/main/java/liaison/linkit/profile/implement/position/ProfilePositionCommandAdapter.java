package liaison.linkit.profile.implement.position;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.domain.repository.position.ProfilePositionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePositionCommandAdapter {

    final ProfilePositionRepository profilePositionRepository;

    public void deleteAllByProfileId(final Long profileId) {
        profilePositionRepository.deleteAllByProfileId(profileId);
    }

    public void save(final ProfilePosition profilePosition) {
        profilePositionRepository.save(profilePosition);
    }
}
