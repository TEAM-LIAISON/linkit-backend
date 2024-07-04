package liaison.linkit.profile.dto.request.profileRegion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRegionCreateRequest {
    // region 관련
    @NotNull(message = "시/도를 선택하세요")
    private String cityName;
    @NotNull(message = "시/군/구를 선택하세요")
    private String divisionName;
}
