package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfilePositionItem;

@Mapper
public class MiniProfileMapper {

    public MiniProfileDetailResponse toMiniProfileDetailResponse(
            final Profile profile,
            final String memberName,
            final ProfilePositionItem profilePositionItem,
            final RegionDetail regionDetail,
            final List<ProfileCurrentStateItem> profileCurrentStateItems
    ) {
        return MiniProfileDetailResponse
                .builder()
                .profileId(profile.getId())
                .profileImagePath(profile.getProfileImagePath())
                .memberName(memberName)
                .profilePositionItem(profilePositionItem)
                .cityName(regionDetail.getCityName())
                .divisionName(regionDetail.getDivisionName())
                .profileCurrentStates(profileCurrentStateItems)
                .isProfilePublic(profile.isProfilePublic())
                .build();
    }

    public MiniProfileResponseDTO.UpdateMiniProfileResponse toUpdateProfile(final Profile profile) {
        return MiniProfileResponseDTO.UpdateMiniProfileResponse.builder()
                .profileId(profile.getId())
                .modifiedAt(profile.getModifiedAt())
                .build();
    }

    public ProfilePositionItem toProfilePositionItem(final ProfilePosition profilePosition) {
        return ProfilePositionItem
                .builder()
                .majorPosition(profilePosition.getPosition().getMajorPosition())
                .subPosition(profilePosition.getPosition().getSubPosition())
                .build();
    }

    public List<ProfileCurrentStateItem> toProfileCurrentStateItems(final List<ProfileCurrentState> profileCurrentStates) {
        return profileCurrentStates.stream()
                .map(profileCurrentState -> {
                    ProfileState profileState = profileCurrentState.getProfileState();
                    return ProfileCurrentStateItem.builder()
                            .profileStateName(profileState.getProfileStateName())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
