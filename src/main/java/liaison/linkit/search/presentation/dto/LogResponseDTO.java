package liaison.linkit.search.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogInformMenus {
        @Builder.Default
        private List<LogInformMenu> logInformMenus = new ArrayList<>();
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogInformDetails {
        private String teamLogoImagePath;
        private String teamName;
        private String memberName;
        private String profileImagePath;

        @Builder
        private LogInformDetails(
                final String teamLogoImagePath,
                final String teamName,
                final String memberName,
                final String profileImagePath
        ) {
            this.teamLogoImagePath = teamLogoImagePath;
            this.teamName = teamName;
            this.memberName = memberName;
            this.profileImagePath = profileImagePath;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogInformMenu {
        // PROFILE || TEAM 판단 변수
        private String logDomainType;

        private LocalDateTime createdAt;
        private String logTitle;
        private String logContent;

        private LogInformDetails logInformDetails;
    }
}
