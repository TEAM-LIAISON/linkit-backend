package liaison.linkit.profile.dto.request;

import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProfileTeamBuildingCreateRequest {
    private final Profile profile;
    private final List<TeamBuildingField> teamBuildingField;
}
