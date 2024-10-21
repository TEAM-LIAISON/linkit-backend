package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.license.ProfileLicenseRepository;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseQueryAdapter {
    private final ProfileLicenseRepository profileLicenseRepository;

    public ProfileLicenseItems getProfileLicenseItems(final Long memberId) {
        return profileLicenseRepository.getProfileLicenseItems(memberId);
    }
}
