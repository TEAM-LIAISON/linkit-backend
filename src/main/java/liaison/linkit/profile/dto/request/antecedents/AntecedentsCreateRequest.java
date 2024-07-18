package liaison.linkit.profile.dto.request.antecedents;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AntecedentsCreateRequest {

    @NotNull(message = "기업명(프로젝트명)을 입력해주세요")
    private final String projectName;

    @NotNull(message = "직무(역할)을 입력해주세요")
    private final String projectRole;

    @NotNull(message = "시작 연도/월을 입력해주세요")
    private final String startDate;

    @NotNull(message = "종료 연도/월을 입력해주세요")
    private final String endDate;

    @NotNull(message = "재직 여부를 선택해주세요")
    private final boolean retirement;

    private final String antecedentsDescription;
}
