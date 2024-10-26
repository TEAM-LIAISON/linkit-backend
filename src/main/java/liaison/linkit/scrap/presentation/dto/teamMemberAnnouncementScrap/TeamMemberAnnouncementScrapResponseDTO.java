package liaison.linkit.scrap.presentation.dto.teamMemberAnnouncementScrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberAnnouncementScrapResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberAnnouncementScrap {

        @Builder.Default
        @JsonProperty("isTeamMemberAnnouncementScrap")
        private boolean like = true;

        private LocalDateTime createdAt;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamMemberAnnouncementScrap {
        @Builder.Default
        @JsonProperty("isTeamMemberAnnouncementScrap")
        private boolean like = false;

        private LocalDateTime deletedAt;
    }
}
