package liaison.linkit.team.dto.request.memberIntroduction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMemberIntroductionCreateRequest {
    @NotNull(message = "팀원의 이름을 입력해주세요")
    private final String teamMemberName;

    @NotNull(message = "팀원의 직무/역할을 입력해주세요")
    private final String teamMemberRole;

    @NotNull(message = "팀원 소개를 입력해주세요")
    private final String teamMemberIntroductionText;
}
