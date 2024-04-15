package liaison.linkit.profile.dto.request;

import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileTeamBuildingUpdateRequest {
    private final Profile profile;
    private final TeamBuildingField teamBuildingField;
}
