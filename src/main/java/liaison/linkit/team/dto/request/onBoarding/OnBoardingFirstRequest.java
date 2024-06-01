package liaison.linkit.team.dto.request.onBoarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingFirstRequest {
    private final List<String> teamBuildingFieldNames;
    private final String teamName;
    private final String sectorName;
    private final String sizeType;
}
