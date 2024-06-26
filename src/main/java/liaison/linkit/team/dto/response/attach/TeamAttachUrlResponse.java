package liaison.linkit.team.dto.response.attach;

import liaison.linkit.team.domain.attach.TeamAttachUrl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamAttachUrlResponse {
    private final Long id;
    private final String teamAttachUrlName;
    private final String teamAttachUrlPath;

    public static TeamAttachUrlResponse getTeamAttachUrl(
            final TeamAttachUrl teamAttachUrl
    ) {
        return new TeamAttachUrlResponse(
                teamAttachUrl.getId(),
                teamAttachUrl.getTeamAttachUrlName(),
                teamAttachUrl.getTeamAttachUrlPath()
        );
    }

}
