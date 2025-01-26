package liaison.linkit.team.presentation.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamHistoryRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamHistoryRequest {
        @NotBlank(message = "팀 연혁 이름은 필수입니다")
        @Size(min = 1, message = "팀 연혁 이름은 1자 이상이어야 합니다")
        private String historyName;

        @NotBlank(message = "팀 프로덕트 시작 기간을 입력해주세요.")
        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$",
                message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String historyStartDate;

        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$",
                message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String historyEndDate;

        @NotNull(message = "진행 중 여부는 필수입니다")
        private Boolean isHistoryInProgress;

        @Size(min = 1, message = "팀 연혁 설명은 1자 이상이어야 합니다")
        private String historyDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamHistoryRequest {

        @NotBlank(message = "팀 연혁 이름은 필수입니다")
        @Size(min = 1, message = "팀 연혁 이름은 1자 이상이어야 합니다")
        private String historyName;

        @NotBlank(message = "팀 연혁 시작 기간을 입력해주세요.")
        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$",
                message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String historyStartDate;

        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$",
                message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String historyEndDate;

        @NotNull(message = "진행 중 여부는 필수입니다")
        private Boolean isHistoryInProgress;

        @Size(min = 1, message = "팀 연혁 설명은 1자 이상이어야 합니다")
        private String historyDescription;
    }
}
