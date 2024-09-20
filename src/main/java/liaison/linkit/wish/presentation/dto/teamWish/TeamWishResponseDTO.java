package liaison.linkit.wish.presentation.dto.teamWish;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamWishResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamWish {
        @Builder.Default
        @JsonProperty("isTeamWish")
        private boolean like = true;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamWish {
        @Builder.Default
        @JsonProperty("isTeamWish")
        private boolean like = false;
    }
}
