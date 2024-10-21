package liaison.linkit.profile.presentation.miniProfile.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MiniProfileResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MiniProfileDetail {
        private String profileImagePath;
        private String memberName;

        @Builder.Default
        private List<ProfilePositionItem> profilePositionList = new ArrayList<>();

        private String cityName;
        private String divisionName;

        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStateList = new ArrayList<>();

        private boolean isProfilePublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePositionItem {
        private String positionName;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileCurrentStateItem {
        private String profileStateName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveMiniProfile {
        private Long miniProfileId;
        private LocalDateTime modifiedAt;
    }

}
