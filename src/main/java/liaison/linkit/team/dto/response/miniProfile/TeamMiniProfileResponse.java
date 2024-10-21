package liaison.linkit.team.dto.response.miniProfile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamMiniProfileResponse {

    private final Long id;
    private final String sectorName;
    private final String sizeType;
    private final String teamName;
    private final String teamProfileTitle;
    private final Boolean isTeamActivate;
    private final String teamLogoImageUrl;
    private final List<String> teamKeywordNames;

    public TeamMiniProfileResponse(final String teamName) {
        this.id = null;
        this.sectorName = null;
        this.sizeType = null;
        this.teamName = teamName;
        this.teamProfileTitle = null;
        this.isTeamActivate = null;
        this.teamLogoImageUrl = null;
        this.teamKeywordNames = null;
    }

    public static TeamMiniProfileResponse personalTeamMiniProfile(final TeamMiniProfile teamMiniProfile, final List<TeamMiniProfileKeyword> teamMiniProfileKeywords) {

        List<String> teamKeywordNames = teamMiniProfileKeywords.stream()
                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
                .toList();

        return new TeamMiniProfileResponse(
                teamMiniProfile.getId(),
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getIsTeamActivate(),
                teamMiniProfile.getTeamLogoImageUrl(),
                teamKeywordNames
        );
    }
}
