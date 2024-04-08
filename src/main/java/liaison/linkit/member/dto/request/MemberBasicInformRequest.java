package liaison.linkit.member.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberBasicInformRequest {
    private final String username;
    private final String contact;
    private final String major;
    private final String job;
    private final String teamBuildingStep;
}
