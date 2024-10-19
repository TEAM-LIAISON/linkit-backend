package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;

public interface ProfileLicenseCustomRepository {
    ProfileLicenseItems findProfileLicenseListDTO(Long memberId);
}
