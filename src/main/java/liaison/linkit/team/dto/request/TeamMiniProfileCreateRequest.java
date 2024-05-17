package liaison.linkit.team.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMiniProfileCreateRequest {

    // 팀 규모 & 업종 분야 필요


    @NotNull(message = "팀 이름을 입력해주세요.")
    private final String teamName;

    @NotNull(message = "팀 한 줄 소개를 입력해주세요.")
    private final String teamOneLineIntroduction;

    private final String teamLink;
}
