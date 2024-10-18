package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.ProfileLicenseRepository;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseList;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseQueryAdapter {
    private final ProfileLicenseRepository profileLicenseRepository;

    public ProfileLicenseList findProfileLicenseListDTO(final Long memberId) {
        return profileLicenseRepository.findProfileLicenseListDTO(memberId);
    }
}
