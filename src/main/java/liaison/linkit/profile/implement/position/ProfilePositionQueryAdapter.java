package liaison.linkit.profile.implement.position;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.domain.repository.position.ProfilePositionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePositionQueryAdapter {
    private final ProfilePositionRepository profilePositionRepository;

    public boolean existsProfilePositionByProfileId(final Long profileId) {
        return profilePositionRepository.existsProfilePositionByProfileId(profileId);
    }

    public ProfilePosition findProfilePositionByProfileId(final Long profileId) {
        return profilePositionRepository.findProfilePositionByProfileId(profileId);
    }
}
