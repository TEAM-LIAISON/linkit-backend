package liaison.linkit.team.dto.request.activity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCreateRequest {
    // 실제 활동 방식 다중 선택 가능 관련
    @NotNull(message = "활동 방식을 1개 이상 선택해주세요")
    private List<String> activityTagNames;

    // region 관련
    @NotNull(message = "활동지역 시/구를 선택해주세요")
    private String cityName;

    @NotNull(message = "활동지역 시/군/구를 선택해주세요")
    private String divisionName;
}
