package liaison.linkit.team.presentation.history.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamHistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamHistoryCalendarResponse {
        private Boolean isMyTeam;

        /**
         * 최종 응답을 담을 필드. 예: [ { "2023" : [ { "01" : [ { ... }, { ... } ] }, { "02" : [ { ... }, {
         * ... } ] } ] }, { "2024" : [...] } ]
         */
        private List<Map<String, List<Map<String, List<TeamHistoryViewItem>>>>> teamHistoryCalendar;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamHistoryDetail {
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
    public static class TeamHistoryViewItem {
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
    public static class TeamHistoryItem {
        private Long teamHistoryId;
        private String historyName;
        private String historyStartDate;
        private String historyEndDate;
        private Boolean isHistoryInProgress;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamHistoryItems {
        @Builder.Default private List<TeamHistoryItem> teamHistoryItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamHistoryResponse {
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
    public static class UpdateTeamHistoryResponse {
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
    public static class RemoveTeamHistoryResponse {
        private Long teamHistoryId;
    }
}
