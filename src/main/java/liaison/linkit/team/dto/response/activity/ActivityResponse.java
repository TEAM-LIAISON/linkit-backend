package liaison.linkit.team.dto.response.activity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ActivityResponse {
    private final List<String> activityTagName;
    private final String cityName;
    private final String divisionName;

    public ActivityResponse(
            final ActivityMethodResponse activityMethodResponse,
            final ActivityRegionResponse activityRegionResponse
    ) {
        this.activityTagName = activityMethodResponse.getActivityTagName();
        this.cityName = activityRegionResponse.getCityName();
        this.divisionName = activityRegionResponse.getDivisionName();
    }

    public ActivityResponse() {
        this.activityTagName = null;
        this.cityName = null;
        this.divisionName = null;
    }
}
