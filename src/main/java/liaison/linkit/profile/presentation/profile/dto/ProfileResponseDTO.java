package liaison.linkit.profile.presentation.profile.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLeftMenu {

        // 프로필 완성도
        @Builder.Default
        private ProfileCompletionMenu profileCompletionMenu = new ProfileCompletionMenu();

        // 프로필 카드
        @Builder.Default
        private ProfileInformMenu profileInformMenu = new ProfileInformMenu();

        // 프로필 관리
        @Builder.Default
        private ProfileBooleanMenu profileBooleanMenu = new ProfileBooleanMenu(
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        );
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileCompletionMenu {
        private int profileCompletion;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInformMenu {
        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();

        private String profileImagePath;
        private String memberName;
        private Boolean isProfilePublic;
        private String majorPosition;

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileBooleanMenu {
        private Boolean isMiniProfile;
        private Boolean isProfileSkill;
        private Boolean isProfileActivity;
        private Boolean isProfilePortfolio;
        private Boolean isProfileEducation;
        private Boolean isProfileAwards;
        private Boolean isProfileLicense;
        private Boolean isProfileLink;
    }
}
