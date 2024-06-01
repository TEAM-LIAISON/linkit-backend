package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileRegionResponse {

    private final String cityName;
    private final String divisionName;

//    public static ProfileRegionResponse of(final ProfileRegion profileRegion) {
//        return new ProfileRegionResponse(
//                profileRegion.getRegion().getCityName(),
//                profileRegion.getRegion().getDivisionName()
//        );
//    }
}
