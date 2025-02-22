package liaison.linkit.common.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.domain.region.Region;

@Mapper
public class RegionMapper {
    public RegionDetail toRegionDetail(final Region region) {
        return RegionDetail.builder()
                .cityName(region.getCityName())
                .divisionName(region.getDivisionName())
                .build();
    }
}
