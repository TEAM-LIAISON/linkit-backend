package liaison.linkit.profile.dto.request.awards;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AwardsCreateRequest {
    @NotNull(message = "수상 부문을 입력해주세요")
    private final String awardsName;

    @NotNull(message = "수상명을 입력해주세요")
    private final String ranking;

    @NotNull(message = "주관 기관을 입력해주세요")
    private final String organizer;

    @NotNull(message = "수상 연도를 입력해주세요")
    private final int awardsYear;

    @NotNull(message = "수상 월을 입력해주세요")
    private final int awardsMonth;

    @NotNull(message = "수상 항목에 대한 설명을 입력해주세요")
    private final String awardsDescription;
}
