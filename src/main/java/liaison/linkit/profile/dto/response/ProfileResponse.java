package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final MiniProfileResponse miniProfileResponse;
    private final CompletionResponse completionResponse;
    private final ProfileIntroductionResponse profileIntroductionResponse;
    private final ProfileSkillResponse profileSkillResponse;
    private final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse;
    private final List<AntecedentsResponse> antecedentsResponse;
    private final List<EducationResponse> educationResponse;
    private final List<AwardsResponse> awardsResponse;
    private final AttachResponse attachResponse;

    public static ProfileResponse profileItems(
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileSkillResponse profileSkillResponse,
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses,
            final AttachResponse attachResponse
    ) {
        return new ProfileResponse(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileSkillResponse,
                profileTeamBuildingFieldResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );
    }
}
