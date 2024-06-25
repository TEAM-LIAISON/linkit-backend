package liaison.linkit.team.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryCreateRequest {
    @NotNull(message = "연혁 한 줄 소개를 입력해주세요.")
    private final String historyOneLineIntroduction;

    @NotNull(message = "시작 연도를 입력해주세요.")
    private final int startYear;

    @NotNull(message = "종료 연도를 입력해주세요.")
    private final int endYear;

    @NotNull(message = "연혁 진행 여부를 선택해주세요")
    private final boolean inProgress;

    @NotNull(message = "연혁 설명을 입력해주세요.")
    private final String historyIntroduction;
}
