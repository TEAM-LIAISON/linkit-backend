package liaison.linkit.team.dto.response;

import liaison.linkit.team.domain.TeamProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamProfileIntroductionResponse {

    private final String teamIntroduction;

    public TeamProfileIntroductionResponse() {
        this.teamIntroduction = null;
    }

    public static TeamProfileIntroductionResponse teamProfileIntroductionResponse(final TeamProfile teamProfile) {
        return new TeamProfileIntroductionResponse(
                teamProfile.getTeamIntroduction()
        );
    }

}
