package liaison.linkit.profile.implement.license;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.repository.license.ProfileLicenseRepository;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.UpdateProfileLicenseRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseCommandAdapter {
    final ProfileLicenseRepository profileLicenseRepository;

    public ProfileLicense addProfileLicense(final ProfileLicense profileLicense) {
        return profileLicenseRepository.save(profileLicense);
    }

    public void removeProfileLicense(final ProfileLicense profileLicense) {
        profileLicenseRepository.delete(profileLicense);
    }

    public ProfileLicense updateProfileLicense(
            final Long profileLicenseId,
            final UpdateProfileLicenseRequest updateProfileLicenseRequest) {
        return profileLicenseRepository.updateProfileLicense(
                profileLicenseId, updateProfileLicenseRequest);
    }

    public void removeProfileLicensesByProfileId(final Long profileId) {
        profileLicenseRepository.removeProfileLicensesByProfileId(profileId);
    }
}
