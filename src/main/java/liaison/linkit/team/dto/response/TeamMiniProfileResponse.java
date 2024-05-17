package liaison.linkit.team.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamMiniProfileResponse {

//    private final IndustrySector industrySector;
//    private final TeamSize teamSize;
    private final String teamName;
    private final String teamOneLineIntroduction;
    private final String teamLink;

//    public static TeamMiniProfileResponse personalTeamMiniProfile(
//            final TeamMiniProfile teamMiniProfile
//    ) {
//        return new TeamMiniProfileResponse(
//                teamMiniProfile.getTeamName(),
//                teamMiniProfile.getTeamOneLineIntroduction(),
//                teamMiniProfile.getTeamLink()
//        );
//    }

}
