package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.skill.ProfileSkillResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingProfileResponse {

    // 모든 온보딩 정보가 전달되는 핵심 비즈니스 로직 변수

    private final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse;
    private final ProfileSkillResponse profileSkillResponse;
    private final ProfileRegionResponse profileRegionResponse;
    private final List<EducationResponse> educationResponses;
    private final List<AntecedentsResponse> antecedentsResponses;
    private final MiniProfileResponse miniProfileResponse;
    private final MemberNameResponse memberNameResponse;

    public static OnBoardingProfileResponse onBoardingProfileItems(
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse,
            final ProfileSkillResponse profileSkillResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<EducationResponse> educationResponses,
            final List<AntecedentsResponse> antecedentsResponses,
            final MiniProfileResponse miniProfileResponse,
            final MemberNameResponse memberNameResponse
    ) {
        return new OnBoardingProfileResponse(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse,
                memberNameResponse
        );
    }

}
