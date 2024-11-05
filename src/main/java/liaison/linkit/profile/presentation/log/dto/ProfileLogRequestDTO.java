package liaison.linkit.profile.presentation.log.dto;

import liaison.linkit.global.type.ProfileLogType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLogRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileLogRequest {
        private String logTitle;
        private String logContent;
        private Boolean isLogPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLogType {
        private ProfileLogType profileLogType;
    }
}

