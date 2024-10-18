package liaison.linkit.wish.presentation.dto.privateWish;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrivateWishResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddPrivateWish {
        @Builder.Default
        @JsonProperty("isPrivateWish")
        private boolean like = true;

        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemovePrivateWish {
        @Builder.Default
        @JsonProperty("isPrivateWish")
        private boolean like = false;

        private LocalDateTime deletedAt;
    }
}
