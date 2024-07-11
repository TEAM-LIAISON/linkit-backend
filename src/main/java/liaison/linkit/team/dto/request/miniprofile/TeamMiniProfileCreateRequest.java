package liaison.linkit.team.dto.request.miniprofile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamMiniProfileCreateRequest {

    @NotNull(message = "팀 소개서 제목을 입력해주세요")
    private final String teamProfileTitle;

    @NotNull(message = "팀을 소개하는 키워드를 알려주세요")
    private final List<String> teamKeywordNames;

    @NotNull(message = "프로필 활성화 여부를 선택해주세요")
    private final Boolean isTeamActivate;
}
