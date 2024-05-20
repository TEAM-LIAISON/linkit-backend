package liaison.linkit.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamProfileTeamBuildingFieldCreateRequest {
    private List<String> teamBuildingFieldNames;
}
