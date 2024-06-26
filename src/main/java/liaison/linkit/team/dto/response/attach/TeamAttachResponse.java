package liaison.linkit.team.dto.response.attach;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamAttachResponse {
    private List<TeamAttachUrlResponse> teamAttachUrlResponseList;
    private List<TeamAttachFileResponse> teamAttachFileResponseList;

    public TeamAttachResponse() {
        this.teamAttachUrlResponseList = null;
        this.teamAttachFileResponseList = null;
    }

    public static TeamAttachResponse getTeamAttachResponse(
            final List<TeamAttachUrlResponse> teamAttachUrlResponses,
            final List<TeamAttachFileResponse> teamAttachFileResponses
    ) {
        return new TeamAttachResponse(
                teamAttachUrlResponses,
                teamAttachFileResponses
        );
    }
}
