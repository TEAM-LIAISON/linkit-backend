package liaison.linkit.profile.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;

@Mapper
public class ProfilePositionMapper {

    public ProfileResponseDTO.ProfilePositionDetail toProfilePositionDetail(
            final ProfilePosition profilePosition
    ) {
        return ProfileResponseDTO.ProfilePositionDetail
                .builder()
                .majorPosition(profilePosition.getPosition().getMajorPosition())
                .subPosition(profilePosition.getPosition().getSubPosition())
                .build();
    }
}
