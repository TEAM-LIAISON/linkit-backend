package liaison.linkit.team.dto.response.onBoarding;

import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingFieldTeamInformResponse {
    private final List<String> teamBuildingFieldNames;
    private final String teamName;
    private final String sectorName;
    private final String sizeType;

    public OnBoardingFieldTeamInformResponse(
            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse,
            final TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse
    ) {
        this.teamBuildingFieldNames = teamProfileTeamBuildingFieldResponse.getTeamProfileTeamBuildingFieldNames();
        this.teamName = teamMiniProfileEarlyOnBoardingResponse.getTeamName();
        this.sectorName = teamMiniProfileEarlyOnBoardingResponse.getSectorName();
        this.sizeType = teamMiniProfileEarlyOnBoardingResponse.getSizeType();
    }

    public OnBoardingFieldTeamInformResponse() {
        this.teamBuildingFieldNames = null;
        this.teamName = null;
        this.sectorName = null;
        this.sizeType = null;
    }
}
