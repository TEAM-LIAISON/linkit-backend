package liaison.linkit.team.presentation.team.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamRequest {
        private String teamName;
        private String teamShortDescription;
        private String scaleName;
        private String cityName;
        private String divisionName;
        private List<String> teamStateNames;
        private Boolean isTeamPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamRequest {
        private String teamName;
        private String teamShortDescription;
        private String scaleName;
        private String cityName;
        private String divisionName;
        private List<String> teamStateNames;
        private boolean isTeamPublic;
    }
}
