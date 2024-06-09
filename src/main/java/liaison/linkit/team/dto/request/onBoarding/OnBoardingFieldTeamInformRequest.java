package liaison.linkit.team.dto.request.onBoarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingFieldTeamInformRequest {

    // 희망 팀빌딩 분야 리스트 형태
    private final List<String> teamBuildingFieldNames;

    // 팀명
    private final String teamName;

    // 팀 규모
    private final String sizeType;

    // 분야
    private final String sectorName;
}


