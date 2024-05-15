package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.dto.response.Attach.AttachFileResponse;
import liaison.linkit.profile.dto.response.Attach.AttachUrlResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final MiniProfileResponse miniProfileResponse;
    private final CompletionResponse completionResponse;
    private final ProfileIntroductionResponse profileIntroductionResponse;
    private final ProfileSkillResponse profileSkillResponse;
    private final ProfileTeamBuildingResponse profileTeamBuildingResponse;
    private final AntecedentsResponse antecedentsResponse;
    // 학력 자리
    private final AwardsResponse awardsResponse;
    private final AttachUrlResponse attachUrlResponse;
    private final AttachFileResponse attachFileResponse;
}
