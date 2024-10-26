package liaison.linkit.profile.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;

@Mapper
public class ProfileMapper {

    public MiniProfileResponseDTO.UpdateMiniProfileResponse toUpdateProfile(final Profile profile) {
        return MiniProfileResponseDTO.UpdateMiniProfileResponse.builder()
                .profileId(profile.getId())
                .modifiedAt(profile.getModifiedAt())
                .build();
    }


}
