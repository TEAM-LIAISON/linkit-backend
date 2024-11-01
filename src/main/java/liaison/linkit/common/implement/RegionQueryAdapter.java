package liaison.linkit.common.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.domain.repository.region.ProfileRegionRepository;
import liaison.linkit.profile.exception.region.ProfileRegionNotFoundException;
import liaison.linkit.team.domain.repository.region.RegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class RegionQueryAdapter {
    private final ProfileRegionRepository profileRegionRepository;

    private final RegionRepository regionRepository;


    public Region findByCityNameAndDivisionName(final String cityName, final String divisionName) {
        return regionRepository.findByCityNameAndDivisionName(cityName, divisionName);
    }

    public ProfileRegion findProfileRegionByProfileId(final Long profileId) {
        return profileRegionRepository.findProfileRegionByProfileId(profileId)
                .orElseThrow(() -> ProfileRegionNotFoundException.EXCEPTION);
    }

    public boolean existsProfileRegionByProfileId(final Long profileId) {
        return profileRegionRepository.existsProfileRegionByProfileId(profileId);
    }

}
