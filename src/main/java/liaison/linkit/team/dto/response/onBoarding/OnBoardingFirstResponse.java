package liaison.linkit.team.dto.response.onBoarding;

import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingFirstResponse {
    private final List<String> teamBuildingFieldNames;
    private final String teamName;
    private final String sectorName;
    private final String sizeType;

    public OnBoardingFirstResponse(
            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse,
            final TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse
    ) {
        this.teamBuildingFieldNames = teamProfileTeamBuildingFieldResponse.getTeamBuildingFieldNames();
        this.teamName = teamMiniProfileEarlyOnBoardingResponse.getTeamName();
        this.sectorName = teamMiniProfileEarlyOnBoardingResponse.getSectorName();
        this.sizeType = teamMiniProfileEarlyOnBoardingResponse.getSizeType();
    }
}
