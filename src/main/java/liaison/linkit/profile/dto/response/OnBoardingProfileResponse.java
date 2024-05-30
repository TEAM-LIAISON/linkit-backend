package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingProfileResponse {

    // 모든 온보딩 정보가 전달되는 핵심 비즈니스 로직 변수

    private final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse;
    private final ProfileSkillResponse profileSkillResponse;
    private final List<EducationResponse> educationResponses;
    private final List<AntecedentsResponse> antecedentsResponses;
    private final MiniProfileResponse miniProfileResponse;

    public static OnBoardingProfileResponse onBoardingProfileItems(
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            final ProfileSkillResponse profileSkillResponse,
            final List<EducationResponse> educationResponses,
            final List<AntecedentsResponse> antecedentsResponses,
            final MiniProfileResponse miniProfileResponse
    ) {
        return new OnBoardingProfileResponse(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );
    }

}
