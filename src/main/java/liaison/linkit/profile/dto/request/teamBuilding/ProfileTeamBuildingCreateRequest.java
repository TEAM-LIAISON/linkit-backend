package liaison.linkit.profile.dto.request.teamBuilding;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileTeamBuildingCreateRequest {
    @NotNull(message = "희망 팀빌딩 분야 항목을 반드시 1개 이상 선택하세요")
    private List<String> teamBuildingFieldNames;
}
