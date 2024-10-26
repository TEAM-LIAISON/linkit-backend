package liaison.linkit.team.domain.repository.region;

import liaison.linkit.profile.domain.region.Region;

public interface RegionRepositoryCustom {
    Region findByCityNameAndDivisionName(final String cityName, final String divisionName);
}
