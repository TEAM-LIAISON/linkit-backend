package liaison.linkit.common.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.domain.repository.region.RegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class RegionQueryAdapter {
    private final RegionRepository regionRepository;

    public Region findByCityNameAndDivisionName(final String cityName, final String divisionName) {
        return regionRepository.findByCityNameAndDivisionName(cityName, divisionName);
    }
}
