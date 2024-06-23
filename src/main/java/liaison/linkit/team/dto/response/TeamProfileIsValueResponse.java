package liaison.linkit.team.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamProfileIsValueResponse {
    // 팀 자기소개 항목
    private final boolean isTeamIntroduction;

    // 팀 프로필 희망 팀빌딩 분야
    private boolean isTeamProfileTeamBuildingField;

    // 활동 방식 및 지역
    private boolean isActivity;


}
