package liaison.linkit.profile.presentation.url.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.global.type.ProfileUrlType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileUrlResponseDTO {


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUrlItem {
        @JsonProperty("profileUrlId")
        private String Id;
        private String urlIconImagePath;
        private String urlName;
        private String urlPath;
        private ProfileUrlType profileUrlType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUrlItems {
        private List<ProfileUrlItem> profileUrlItems;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileUrl {
        private Long profileUrlId;
        private LocalDateTime deletedAt;
    }
}
