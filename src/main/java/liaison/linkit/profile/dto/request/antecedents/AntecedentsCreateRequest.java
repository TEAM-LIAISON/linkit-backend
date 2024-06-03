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

    @NotNull(message = "시작 연도를 입력해주세요")
    private final int startYear;

    @NotNull(message = "시작 월을 입력해주세요")
    private final int startMonth;

    @NotNull(message = "종료 연도를 입력해주세요")
    private final int endYear;

    @NotNull(message = "종료 월을 입력해주세요")
    private final int endMonth;

    @NotNull(message = "재직 여부를 선택해주세요")
    private final boolean retirement;
}
