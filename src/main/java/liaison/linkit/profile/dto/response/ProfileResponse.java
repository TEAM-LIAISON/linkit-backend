package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final MiniProfileResponse miniProfileResponse;
    private final CompletionResponse completionResponse;
    private final ProfileIntroductionResponse profileIntroductionResponse;
    private final JobAndSkillResponse jobAndSkillResponse;
    private final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse;
    private final ProfileRegionResponse profileRegionResponse;
    private final List<AntecedentsResponse> antecedentsResponse;
    private final List<EducationResponse> educationResponse;
    private final List<AwardsResponse> awardsResponse;
    private final AttachResponse attachResponse;

    public static ProfileResponse profileItems(
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final JobAndSkillResponse jobAndSkillResponse,
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses,
            final AttachResponse attachResponse
    ) {

        return new ProfileResponse(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                jobAndSkillResponse,
                profileTeamBuildingFieldResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );
    }
}
