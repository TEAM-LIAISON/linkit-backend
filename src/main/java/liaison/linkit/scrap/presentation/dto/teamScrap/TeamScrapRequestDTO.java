package liaison.linkit.scrap.presentation.dto.teamScrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamScrapRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamScrapRequest {
        private boolean changeScrapValue;
    }

}
