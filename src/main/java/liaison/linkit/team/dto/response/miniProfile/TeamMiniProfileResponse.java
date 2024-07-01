package liaison.linkit.team.dto.response.miniProfile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private final List<String> teamKeywordNames;

    public TeamMiniProfileResponse() {
        this.sectorName = null;
        this.sizeType = null;
        this.teamName = null;
        this.miniProfileTitle = null;
        this.teamUploadPeriod = null;
        this.teamUploadDeadline = null;
        this.teamLogoImageUrl = null;
        this.teamKeywordNames = null;
    }

    public static TeamMiniProfileResponse personalTeamMiniProfile(final TeamMiniProfile teamMiniProfile) {
        final List<String> keywordNames = teamMiniProfile.getTeamMiniProfileKeywordArrayList().stream()
                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
                .toList();

        return new TeamMiniProfileResponse(
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getTeamUploadPeriod(),
                teamMiniProfile.getTeamUploadDeadline(),
                teamMiniProfile.getTeamLogoImageUrl(),
                keywordNames
        );
    }
}
