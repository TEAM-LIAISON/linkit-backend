package liaison.linkit.profile.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AwardsUpdateRequest {
    @NotNull(message = "수상 부문을 입력해주세요")
    @Size(max = 50, message = "수상 부문은 50자를 넘을 수 없습니다.")
    private final String awardsName;

    @NotNull(message = "수상명을 입력해주세요")
    private final String ranking;

    @NotNull(message = "주관 기관을 입력해주세요")
    private final String organizer;

    @NotNull(message = "수상 연도를 입력해주세요")
    private final int awardsYear;

    @NotNull(message = "수상 월을 입력해주세요")
    private final int awardsMonth;

    @NotNull(message = "수상 이력에 대한 설명을 입력해주세요")
    @Size(max = 200, message = "수상 이력은 200자를 넘을 수 없습니다.")
    private final String awardsDescription;
}
