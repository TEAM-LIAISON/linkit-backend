package liaison.linkit.profile.implement.region;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.repository.region.ProfileRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileRegionCommandAdapter {
    final ProfileRegionRepository profileRegionRepository;

    public void deleteByProfileId(final Long profileId) {
        profileRegionRepository.deleteByProfileId(profileId);
    }

    public void save(final ProfileRegion profileRegion) {
        profileRegionRepository.save(profileRegion);
    }
}
