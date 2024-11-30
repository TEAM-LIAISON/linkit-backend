package liaison.linkit.team.presentation.history.dto;

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
        private Long teamHistoryId;
        private String historyName;
        private String historyStartDate;
        private String historyEndDate;
        private Boolean isHistoryInProgress;
        private String historyDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamHistoryRequest {
        private Long teamHistoryId;
        private String historyName;
        private String historyStartDate;
        private String historyEndDate;
        private Boolean isHistoryInProgress;
        private String historyDescription;
    }
}
