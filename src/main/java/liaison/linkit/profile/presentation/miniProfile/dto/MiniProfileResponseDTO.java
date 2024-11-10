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
    public static class MiniProfileDetailResponse {

        private Long profileId;
        private String profileImagePath;
        private String memberName;
        private ProfilePositionItem profilePositionItem;

        private String cityName;
        private String divisionName;

        private ProfileCurrentStateItems profileCurrentStateItems;

        private Boolean isProfilePublic;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePositionItem {
        private String majorPosition;
        private String subPosition;
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
    public static class ProfileCurrentStateItems {
        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMiniProfileResponse {
        private Long profileId;
        private LocalDateTime modifiedAt;
        private String majorPosition;
        private String subPosition;
        private String cityName;
        private String divisionName;
        private List<String> profileStateNames;
        private Boolean isProfilePublic;
    }
}
