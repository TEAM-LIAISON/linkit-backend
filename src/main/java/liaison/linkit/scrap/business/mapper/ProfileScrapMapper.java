package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO.UpdateProfileScrap;

@Mapper
public class ProfileScrapMapper {

    public ProfileScrapResponseDTO.UpdateProfileScrap toUpdateProfileScrap(
            final String emailId, final boolean changeScrapValue) {
        return UpdateProfileScrap.builder()
                .emailId(emailId)
                .isProfileScrap(changeScrapValue)
                .build();
    }
}
