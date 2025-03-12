package liaison.linkit.search.presentation.dto;

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
        @Builder.Default private List<LogInformMenu> logInformMenus = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogInformMenu {
        @Builder.Default private Long id = 0L;
        @Builder.Default private String domainType = "";

        @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
        @Builder.Default private String logTitle = "";
        @Builder.Default private String logContent = "";

        @Builder.Default private LogInformDetails logInformDetails = new LogInformDetails();
    }

    @Getter
    @Builder
    @NoArgsConstructor // 기본 생성자 추가
    public static class LogInformDetails {
        @Builder.Default private String teamCode = "";
        @Builder.Default private Long teamLogId = 0L;
        @Builder.Default private String teamLogoImagePath = "";
        @Builder.Default private String teamName = "";

        @Builder.Default private String emailId = "";
        @Builder.Default private Long profileLogId = 0L;
        @Builder.Default private String memberName = "";
        @Builder.Default private String profileImagePath = "";

        @Builder
        private LogInformDetails(
                final String teamCode,
                final Long teamLogId,
                final String teamLogoImagePath,
                final String teamName,
                final String emailId,
                final Long profileLogId,
                final String memberName,
                final String profileImagePath) {
            this.teamCode = teamCode != null ? teamCode : "";
            this.teamLogId = teamLogId != null ? teamLogId : 0L;
            this.teamLogoImagePath = teamLogoImagePath != null ? teamLogoImagePath : "";
            this.teamName = teamName != null ? teamName : "";

            this.emailId = emailId != null ? emailId : "";
            this.profileLogId = profileLogId != null ? profileLogId : 0L;
            this.memberName = memberName != null ? memberName : "";
            this.profileImagePath = profileImagePath != null ? profileImagePath : "";
        }

        public static LogInformDetails profileLogType(
                final String emailId,
                final Long profileLogId,
                final String memberName,
                final String profileImagePath) {
            return LogInformDetails.builder()
                    .emailId(emailId)
                    .profileLogId(profileLogId)
                    .memberName(memberName)
                    .profileImagePath(profileImagePath)
                    .build();
        }

        public static LogInformDetails teamLogType(
                final String teamCode,
                final Long teamLogId,
                final String teamName,
                final String teamLogoImagePath) {
            return LogInformDetails.builder()
                    .teamCode(teamCode)
                    .teamLogId(teamLogId)
                    .teamName(teamName)
                    .teamLogoImagePath(teamLogoImagePath)
                    .build();
        }
    }
}
