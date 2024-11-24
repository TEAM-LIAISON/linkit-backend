package liaison.linkit.profile.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;

@Mapper
public class ProfileMapper {


    public ProfileResponseDTO.ProfileLeftMenu toProfileLeftMenu(
            final ProfileCompletionMenu profileCompletionMenu,
            final ProfileInformMenu profileInformMenu,
            final ProfileBooleanMenu profileBooleanMenu
    ) {
        return ProfileResponseDTO.ProfileLeftMenu
                .builder()
                .profileCompletionMenu(profileCompletionMenu)
                .profileInformMenu(profileInformMenu)
                .profileBooleanMenu(profileBooleanMenu)
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

    public ProfileResponseDTO.ProfileInformMenu toProfileInformMenu(
            final List<ProfileCurrentStateItem> profileCurrentStateItems,
            final Profile profile,
            final ProfilePositionDetail profilePositionDetail,
            final RegionDetail regionDetail
    ) {
        return ProfileResponseDTO.ProfileInformMenu
                .builder()
                .profileCurrentStates(profileCurrentStateItems)
                .profileImagePath(profile.getProfileImagePath())
                .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                .isProfilePublic(profile.isProfilePublic())
                .majorPosition(profilePositionDetail.getMajorPosition())
                .regionDetail(regionDetail)
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
                .isProfilePortfolio(profile.isProfileEducation())
                .isProfileEducation(profile.isProfileEducation())
                .isProfileAwards(profile.isProfileAwards())
                .isProfileLicense(profile.isProfileLicense())
                .isProfileLink(profile.isProfileLink())
                .build();
    }
}
