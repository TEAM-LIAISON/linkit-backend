package liaison.linkit.team.presentation.log.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamLogRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamLogRequest {
        
        @NotBlank(message = "로그 제목은 필수입니다")
        @Size(min = 1, max = 100, message = "로그 제목은 1자 이상 100자 이하여야 합니다")
        private String logTitle;

        @NotBlank(message = "로그 내용은 필수입니다")
        @Size(min = 1, message = "로그 내용은 1자 이상이어야 합니다")
        private String logContent;

        @NotNull(message = "공개 여부는 필수입니다")
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogRequest {

        @NotBlank(message = "로그 제목은 필수입니다")
        @Size(min = 1, max = 100, message = "로그 제목은 1자 이상 100자 이하여야 합니다")
        private String logTitle;

        @NotBlank(message = "로그 내용은 필수입니다")
        @Size(min = 1, message = "로그 내용은 1자 이상이어야 합니다")
        private String logContent;

        @NotNull(message = "공개 여부는 필수입니다")        
        private Boolean isLogPublic;
        
    }
}
