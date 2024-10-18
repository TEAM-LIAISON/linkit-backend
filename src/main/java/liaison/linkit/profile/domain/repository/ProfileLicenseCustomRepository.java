package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;

public interface ProfileLicenseCustomRepository {
    ProfileLicenseResponseDTO.ProfileLicenseList findProfileLicenseListDTO(Long memberId);
}
