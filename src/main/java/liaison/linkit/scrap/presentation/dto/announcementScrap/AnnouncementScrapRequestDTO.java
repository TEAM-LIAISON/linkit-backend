package liaison.linkit.scrap.presentation.dto.announcementScrap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnouncementScrapRequestDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAnnouncementScrapRequest {
        private boolean changeScrapValue;
    }
}
