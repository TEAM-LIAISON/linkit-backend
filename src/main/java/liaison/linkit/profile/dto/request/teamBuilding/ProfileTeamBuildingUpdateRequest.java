package liaison.linkit.profile.dto.request.teamBuilding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileTeamBuildingUpdateRequest {
    private List<String> teamBuildingFieldNames;
}
