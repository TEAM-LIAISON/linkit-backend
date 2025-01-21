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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogInformMenu {
        private Long id;
        private String domainType;

        private LocalDateTime createdAt;
        private String logTitle;
        private String logContent;

        private LogInformDetails logInformDetails;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogInformDetails {
        private String teamCode;
        private String teamLogoImagePath;
        private String teamName;

        private String emailId;
        private String memberName;
        private String profileImagePath;

        @Builder
        private LogInformDetails(
                final String teamCode,
                final String teamLogoImagePath,
                final String teamName,

                final String emailId,
                final String memberName,
                final String profileImagePath
        ) {
            this.teamCode = teamCode;
            this.teamLogoImagePath = teamLogoImagePath;
            this.teamName = teamName;

            this.emailId = emailId;
            this.memberName = memberName;
            this.profileImagePath = profileImagePath;
        }

        public static LogInformDetails profileLogType(final String emailId, final String memberName, final String profileImagePath) {
            return LogInformDetails.builder()
                    .emailId(emailId)
                    .memberName(memberName)
                    .profileImagePath(profileImagePath)
                    .build();
        }

        public static LogInformDetails teamLogType(final String teamCode, final String teamName, final String teamLogoImagePath) {
            return LogInformDetails.builder()
                    .teamCode(teamCode)
                    .teamName(teamName)
                    .teamLogoImagePath(teamLogoImagePath)
                    .build();
        }
    }
}
