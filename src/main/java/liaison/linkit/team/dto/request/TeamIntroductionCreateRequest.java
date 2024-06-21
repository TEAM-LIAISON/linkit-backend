package liaison.linkit.team.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TeamIntroductionCreateRequest {
    @NotNull(message = "팀 소개를 반드시 입력해주세요")
    private final String teamIntroduction;

    public TeamIntroductionCreateRequest(final String teamIntroduction) {
        this.teamIntroduction = teamIntroduction;
    }
}
