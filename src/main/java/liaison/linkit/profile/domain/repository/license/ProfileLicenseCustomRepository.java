package liaison.linkit.profile.domain.repository.license;

import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;

public interface ProfileLicenseCustomRepository {
    ProfileLicenseItems getProfileLicenseItems(Long memberId);
}
