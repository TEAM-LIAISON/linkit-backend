package liaison.linkit.team.presentation.log.dto;

import liaison.linkit.profile.domain.type.LogType;
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
        private String logTitle;
        private String logContent;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogType {
        private LogType logType;
    }
}
