package liaison.linkit.team.dto.request.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCreateRequest {

    // activityMethodTag 관련
    private List<String> activityTagNames;

    // region 관련
    private String cityName;
    private String divisionName;
}
