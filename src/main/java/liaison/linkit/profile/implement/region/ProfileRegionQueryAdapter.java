package liaison.linkit.profile.implement.region;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.region.ProfileRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileRegionQueryAdapter {
    final ProfileRegionRepository profileRegionRepository;

    public boolean existsProfileRegionByProfileId(final Long profileId) {
        return profileRegionRepository.existsProfileRegionByProfileId(profileId);
    }
}
