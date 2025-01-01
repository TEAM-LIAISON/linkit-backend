package liaison.linkit.profile.business.mapper;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenus;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileScrapMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;

@Mapper
public class ProfileMapper {

    public ProfileResponseDTO.ProfileLeftMenu toProfileLeftMenu(
            final ProfileCompletionMenu profileCompletionMenu,
            final ProfileInformMenu profileInformMenu,
            final ProfileBooleanMenu profileBooleanMenu,
            final ProfileScrapMenu profileScrapMenu
    ) {
        return ProfileResponseDTO.ProfileLeftMenu
                .builder()
                .profileCompletionMenu(profileCompletionMenu)
                .profileInformMenu(profileInformMenu)
                .profileBooleanMenu(profileBooleanMenu)
                .profileScrapMenu(profileScrapMenu)
                .build();
    }

    public ProfileResponseDTO.ProfileScrapMenu toProfileScrapMenu(
            final Profile profile
    ) {
        return ProfileScrapMenu
                .builder()
                .profileScrapCount(profile.getMember().getProfileScrapCount())
                .build();
    }

    public ProfileResponseDTO.ProfileCompletionMenu toProfileCompletionMenu(
            final Profile profile
    ) {
        return ProfileResponseDTO.ProfileCompletionMenu
                .builder()
                .profileCompletion(profile.getProfileCompletion())
                .build();
    }

    public ProfileResponseDTO.ProfileInformMenus toProfileInformMenus(
            final List<ProfileInformMenu> profileInformMenus
    ) {
        return ProfileInformMenus.builder()
                .profileInformMenus(profileInformMenus)
                .build();
    }

    public ProfileResponseDTO.ProfileInformMenu toProfileInformMenu(
            final List<ProfileCurrentStateItem> profileCurrentStateItems,
            final boolean isProfileScrap,
            final Profile profile,
            final ProfilePositionDetail profilePositionDetail,
            final RegionDetail regionDetail,
            final List<ProfileTeamInform> profileTeamInforms
    ) {
        return ProfileResponseDTO.ProfileInformMenu
                .builder()
                .profileCurrentStates(profileCurrentStateItems)
                .isProfileScrap(isProfileScrap)
                .profileImagePath(profile.getProfileImagePath())
                .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                .emailId(profile.getMember().getEmailId())
                .isProfilePublic(profile.isProfilePublic())
                .majorPosition(profilePositionDetail.getMajorPosition())
                .regionDetail(regionDetail)
                .profileTeamInforms(profileTeamInforms)
                .build();
    }


    public ProfileResponseDTO.ProfileBooleanMenu toProfileBooleanMenu(
            final Profile profile
    ) {
        return ProfileResponseDTO.ProfileBooleanMenu
                .builder()
                .isMiniProfile(profile.isProfileMiniProfile())
                .isProfileSkill(profile.isProfileSkill())
                .isProfileActivity(profile.isProfileActivity())
                .isProfilePortfolio(profile.isProfilePortfolio())
                .isProfileEducation(profile.isProfileEducation())
                .isProfileAwards(profile.isProfileAwards())
                .isProfileLicense(profile.isProfileLicense())
                .isProfileLink(profile.isProfileLink())
                .build();
    }

    public ProfileResponseDTO.ProfileDetail toProfileDetail(
            final boolean isMyProfile,
            final ProfileCompletionMenu profileCompletionMenu,
            final ProfileInformMenu profileInformMenu,
            final int profileScrapCount,
            final ProfileLogItem profileLogItem,
            final List<ProfileSkillItem> profileSkillItems,
            final List<ProfileActivityItem> profileActivityItems,
            final List<ProfilePortfolioItem> profilePortfolioItems,
            final List<ProfileEducationItem> profileEducationItems,
            final List<ProfileAwardsItem> profileAwardsItems,
            final List<ProfileLicenseItem> profileLicenseItems,
            final List<ProfileLinkItem> profileLinkItems // 타입 변경
    ) {
        return ProfileResponseDTO.ProfileDetail
                .builder()
                .isMyProfile(isMyProfile)
                .profileCompletionMenu(profileCompletionMenu)
                .profileInformMenu(profileInformMenu)
                .profileScrapCount(profileScrapCount)
                .profileLogItem(profileLogItem)
                .profileSkillItems(profileSkillItems)
                .profileActivityItems(profileActivityItems)
                .profilePortfolioItems(profilePortfolioItems)
                .profileEducationItems(profileEducationItems)
                .profileAwardsItems(profileAwardsItems)
                .profileLicenseItems(profileLicenseItems)
                .profileLinkItems(profileLinkItems)  // 수정된 필드 사용
                .build();
    }


}
