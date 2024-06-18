package liaison.linkit.team.dto.response;

import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OnBoardingTeamProfileResponse {

    private final OnBoardingFieldTeamInformResponse onBoardingFieldTeamInformResponse;
    private final ActivityResponse activityResponse;
    private final TeamMiniProfileResponse teamMiniProfileResponse;

    public static OnBoardingTeamProfileResponse onBoardingTeamProfileItems(
            final OnBoardingFieldTeamInformResponse onBoardingFieldTeamInformResponse,
            final ActivityResponse activityResponse,
            final TeamMiniProfileResponse teamMiniProfileResponse
    ) {
        return new OnBoardingTeamProfileResponse(
                onBoardingFieldTeamInformResponse,
            activityResponse,
            teamMiniProfileResponse
        );
    }
}
