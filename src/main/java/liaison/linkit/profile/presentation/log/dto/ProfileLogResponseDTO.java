package liaison.linkit.profile.presentation.log.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.global.type.ProfileLogType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLogResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLogItems {

        @Builder.Default
        private List<ProfileLogItem> profileLogItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLogItem {

        private Long profileLogId;
        private Boolean isLogPublic;
        private ProfileLogType profileLogType;
        private LocalDateTime modifiedAt;
        private String logTitle;
        private String logContent;

    }
}
