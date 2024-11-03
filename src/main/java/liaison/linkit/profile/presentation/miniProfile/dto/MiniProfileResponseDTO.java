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
        private List<ProfilePositionItem> profilePositions = new ArrayList<>();

        private String cityName;
        private String divisionName;

        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();

        private Boolean isProfilePublic;

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
    public static class UpdateMiniProfileResponse {
        private Long profileId;
        private LocalDateTime modifiedAt;
    }

}
