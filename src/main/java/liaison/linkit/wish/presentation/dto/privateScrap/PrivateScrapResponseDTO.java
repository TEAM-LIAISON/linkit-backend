package liaison.linkit.wish.presentation.dto.privateScrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrivateScrapResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddPrivateScrap {
        @Builder.Default
        @JsonProperty("isPrivateScrap")
        private boolean like = true;

        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemovePrivateScrap {
        @Builder.Default
        @JsonProperty("isPrivateScrap")
        private boolean like = false;

        private LocalDateTime deletedAt;
    }
}
