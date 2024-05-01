package liaison.linkit.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileTeamBuildingCreateRequest {

    private List<String> teamBuildingFieldNames;
}
