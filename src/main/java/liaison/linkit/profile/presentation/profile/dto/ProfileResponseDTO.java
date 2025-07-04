package liaison.linkit.profile.presentation.profile.dto;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.presentation.RegionResponseDTO;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
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

        @Builder.Default
        private ProfileCompletionMenu profileCompletionMenu = new ProfileCompletionMenu();

        @Builder.Default
        private ProfileBooleanMenu profileBooleanMenu =
                new ProfileBooleanMenu(false, false, false, false, false, false, false, false);
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
    public static class ProfileInformMenus {

        @Builder.Default
        private List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInformMenu {

        @Builder.Default private Boolean isProfilePublic = false;

        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();

        @Builder.Default private Boolean isProfileScrap = false;
        @Builder.Default private int profileScrapCount = 0;

        @Builder.Default private String profileImagePath = "";
        @Builder.Default private String memberName = "";
        @Builder.Default private String emailId = "";

        @Builder.Default private String majorPosition = "";
        @Builder.Default private String subPosition = "";

        @Builder.Default private RegionDetail regionDetail = new RegionDetail();

        @Builder.Default private List<ProfileTeamInform> profileTeamInforms = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileSummaryInform {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfileResponseDTO.ProfilePositionDetail profilePositionDetail =
                new ProfileResponseDTO.ProfilePositionDetail();

        @Builder.Default
        private RegionResponseDTO.RegionDetail regionDetail = new RegionResponseDTO.RegionDetail();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileTeamInform {

        private String teamName;
        private String teamCode;
        private String teamLogoImagePath;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileScrapMenu {

        private int profileScrapCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePositionDetail {

        private String majorPosition;
        private String subPosition;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDetail {

        private Boolean isMyProfile;

        @Builder.Default
        private ProfileCompletionMenu profileCompletionMenu =
                new ProfileCompletionMenu(); // 프로필 완성도

        @Builder.Default
        private ProfileInformMenu profileInformMenu = new ProfileInformMenu(); // 프로필 카드

        private ProfileLogItem profileLogItem;
        private List<ProfileSkillItem> profileSkillItems;
        private List<ProfileActivityItem> profileActivityItems;
        private List<ProfilePortfolioItem> profilePortfolioItems;
        private List<ProfileEducationItem> profileEducationItems;
        private List<ProfileAwardsItem> profileAwardsItems;
        private List<ProfileLicenseItem> profileLicenseItems;
        private List<ProfileLinkItem> profileLinkItems;
    }
}
