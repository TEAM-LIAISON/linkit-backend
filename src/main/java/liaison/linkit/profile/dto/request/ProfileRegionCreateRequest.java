package liaison.linkit.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRegionCreateRequest {
    // region 관련
    private String cityName;
    private String divisionName;
}
