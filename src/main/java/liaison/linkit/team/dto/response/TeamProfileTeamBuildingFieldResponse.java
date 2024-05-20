package liaison.linkit.team.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamProfileTeamBuildingFieldResponse {
    private final List<String> teamBuildingFieldNames;

    public static TeamProfileTeamBuildingFieldResponse of(final List<String> profileTeamBuildingFieldNames) {
        return new TeamProfileTeamBuildingFieldResponse(
                profileTeamBuildingFieldNames
        );
    }
}
