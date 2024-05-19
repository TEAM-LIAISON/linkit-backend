package liaison.linkit.team.dto.response;

import liaison.linkit.profile.dto.response.ProfileTeamBuildingResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamProfileTeamBuildingFieldResponse {
    private final List<String> teamBuildingFieldNames;

    public static ProfileTeamBuildingResponse of(final List<String> profileTeamBuildingFieldNames) {
        return new ProfileTeamBuildingResponse(
                profileTeamBuildingFieldNames
        );
    }
}
