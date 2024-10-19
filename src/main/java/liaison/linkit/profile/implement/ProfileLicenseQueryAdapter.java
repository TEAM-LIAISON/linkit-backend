package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.ProfileLicenseRepository;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseQueryAdapter {
    private final ProfileLicenseRepository profileLicenseRepository;

    public ProfileLicenseItems findProfileLicenseItemsDTO(final Long memberId) {
        return profileLicenseRepository.findProfileLicenseItemsDTO(memberId);
    }
}
