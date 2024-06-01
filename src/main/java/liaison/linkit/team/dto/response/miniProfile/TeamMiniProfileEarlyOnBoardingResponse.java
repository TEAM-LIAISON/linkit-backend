package liaison.linkit.team.dto.response.miniProfile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamMiniProfileEarlyOnBoardingResponse {
    private final String teamName;
    private final String sectorName;
    private final String sizeType;

    public static TeamMiniProfileEarlyOnBoardingResponse personalTeamMiniProfileOnBoarding(
            final TeamMiniProfile teamMiniProfile
    ) {
        return new TeamMiniProfileEarlyOnBoardingResponse(
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType()
        );
    }
}
