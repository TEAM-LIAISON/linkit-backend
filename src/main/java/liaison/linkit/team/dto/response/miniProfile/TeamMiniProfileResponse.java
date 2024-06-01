package liaison.linkit.team.dto.response.miniProfile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TeamMiniProfileResponse {

    private final String sectorName;
    private final String sizeType;
    private final String teamName;
    private final String miniProfileTitle;
    private final LocalDateTime teamUploadPeriod;
    private final Boolean teamUploadDeadline;
    private final String teamLogoImage;
    private final String teamValue;
    private final String teamDetailInform;

    public static TeamMiniProfileResponse personalTeamMiniProfile(final TeamMiniProfile teamMiniProfile) {
        return new TeamMiniProfileResponse(
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getMiniProfileTitle(),
                teamMiniProfile.getTeamUploadPeriod(),
                teamMiniProfile.getTeamUploadDeadline(),
                teamMiniProfile.getTeamLogoImage(),
                teamMiniProfile.getTeamValue(),
                teamMiniProfile.getTeamDetailInform()
        );
    }

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
