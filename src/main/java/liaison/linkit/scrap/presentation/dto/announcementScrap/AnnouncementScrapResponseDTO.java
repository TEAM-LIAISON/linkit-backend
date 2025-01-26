package liaison.linkit.scrap.presentation.dto.announcementScrap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnouncementScrapResponseDTO {
    
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAnnouncementScrap {
        private Long teamMemberAnnouncementId;
        private Boolean isAnnouncementScrap;
    }
}
