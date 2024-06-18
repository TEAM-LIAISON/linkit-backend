package liaison.linkit.team.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamProfileTeamBuildingFieldResponse {
    private final List<String> teamProfileTeamBuildingFieldNames;

    public static TeamProfileTeamBuildingFieldResponse of(final List<String> teamProfileTeamBuildingFieldNames) {
        return new TeamProfileTeamBuildingFieldResponse(
                teamProfileTeamBuildingFieldNames
        );
    }
}
