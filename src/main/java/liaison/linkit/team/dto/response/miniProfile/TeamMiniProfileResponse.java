package liaison.linkit.team.dto.response.miniProfile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class TeamMiniProfileResponse {

    private final String sectorName;
    private final String sizeType;
    private final String teamName;
    private final String miniProfileTitle;
    private final LocalDate teamUploadPeriod;
    private final Boolean teamUploadDeadline;
    private final String teamLogoImageUrl;
    private final String teamValue;
    private final String teamDetailInform;

    public TeamMiniProfileResponse() {
        this.sectorName = null;
        this.sizeType = null;
        this.teamName = null;
        this.miniProfileTitle = null;
        this.teamUploadPeriod = null;
        this.teamUploadDeadline = null;
        this.teamLogoImageUrl = null;
        this.teamValue = null;
        this.teamDetailInform = null;
    }

    public static TeamMiniProfileResponse personalTeamMiniProfile(final TeamMiniProfile teamMiniProfile) {
        return new TeamMiniProfileResponse(
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getTeamUploadPeriod(),
                teamMiniProfile.getTeamUploadDeadline(),
                teamMiniProfile.getTeamLogoImageUrl(),
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
