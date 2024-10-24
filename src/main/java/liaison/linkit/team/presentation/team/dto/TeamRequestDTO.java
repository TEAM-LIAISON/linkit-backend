package liaison.linkit.team.presentation.team.dto;

import jakarta.validation.constraints.NotNull;
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
    public static class SaveTeamBasicInformRequest {

        @NotNull
        private String teamName;

        @NotNull
        private String teamShortDescription;
        
        @NotNull
        private String cityName;

        @NotNull
        private String divisionName;

    }
}
