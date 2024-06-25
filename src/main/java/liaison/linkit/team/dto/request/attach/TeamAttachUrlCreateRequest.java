package liaison.linkit.team.dto.request.attach;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamAttachUrlCreateRequest {
    @NotNull(message = "팀 첨부 URL 제목을 입력해주세요")
    private final String teamAttachUrlName;

    @NotNull(message = "팀 첨부 URL을 입력해주세요.")
    private final String teamAttachUrlPath;
}
