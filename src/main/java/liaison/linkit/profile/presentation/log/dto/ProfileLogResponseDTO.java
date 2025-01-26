package liaison.linkit.profile.presentation.log.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.profile.domain.type.LogType;
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
        private LogType logType;
        private LocalDateTime modifiedAt;
        private String logTitle;
        private String logContent;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileLogBodyImageResponse {
        private String profileLogBodyImagePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileLogResponse {
        private Long profileLogId;
        private String logTitle;
        private String logContent;
        private LocalDateTime createdAt;
        private LogType logType;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLogResponse {
        private Long profileLogId;
        private String logTitle;
        private String logContent;
        private LocalDateTime createdAt;
        private LogType logType;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileLogResponse {
        private Long profileLogId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLogTypeResponse {
        private Long profileLogId;
        private LogType logType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLogPublicStateResponse {
        private Long profileLogId;
        private Boolean isLogPublic;
    }
}
