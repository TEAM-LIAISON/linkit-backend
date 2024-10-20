package liaison.linkit.scrap.presentation.dto.teamScrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamScrapResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamScrap {
        @Builder.Default
        @JsonProperty("isTeamScrap")
        private boolean like = true;

        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamScrap {
        @Builder.Default
        @JsonProperty("isTeamScrap")
        private boolean like = false;
    }
}
