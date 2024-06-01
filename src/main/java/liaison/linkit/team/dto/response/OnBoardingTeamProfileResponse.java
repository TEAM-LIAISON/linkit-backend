package liaison.linkit.team.dto.response;

import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFirstResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OnBoardingTeamProfileResponse {

    private final OnBoardingFirstResponse onBoardingFirstResponse;
    private final ActivityResponse activityResponse;
    private final TeamMiniProfileResponse teamMiniProfileResponse;

    public static OnBoardingTeamProfileResponse onBoardingTeamProfileItems(
            final OnBoardingFirstResponse onBoardingFirstResponse,
            final ActivityResponse activityResponse,
            final TeamMiniProfileResponse teamMiniProfileResponse
    ) {
        return new OnBoardingTeamProfileResponse(
            onBoardingFirstResponse,
            activityResponse,
            teamMiniProfileResponse
        );
    }
}
