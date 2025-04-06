package liaison.linkit.team.presentation.log.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import liaison.linkit.profile.domain.type.LogType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamLogResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamLogItems {
        @Builder.Default
        private List<TeamLogResponseDTO.TeamLogItem> teamLogItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamLogRepresentItem {
        private Boolean isTeamManager;

        @Builder.Default
        private List<TeamLogResponseDTO.TeamLogItem> teamLogItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TeamLogItem {
        private Boolean isTeamManager;
        private Long teamLogId;
        private Boolean isLogPublic;
        private LogType logType;
        private LocalDateTime modifiedAt;
        private String logTitle;
        private String logContent;
        private Long logViewCount;
        private Long commentCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamLogResponse {
        private Long teamLogId;
        private String logTitle;
        private String logContent;
        private LocalDateTime createdAt;
        private LogType logType;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogResponse {
        private Long teamLogId;
        private String logTitle;
        private String logContent;
        private LocalDateTime createdAt;
        private LogType logType;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamLogResponse {
        private Long teamLogId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogTypeResponse {
        private Long teamLogId;
        private LogType logType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogPublicStateResponse {
        private Long teamLogId;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamLogBodyImageResponse {
        private String teamLogBodyImagePath;
    }
}
