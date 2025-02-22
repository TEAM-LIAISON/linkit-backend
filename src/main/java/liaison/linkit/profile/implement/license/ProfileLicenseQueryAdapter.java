package liaison.linkit.profile.implement.license;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.repository.license.ProfileLicenseRepository;
import liaison.linkit.profile.exception.license.ProfileLicenseNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseQueryAdapter {
    private final ProfileLicenseRepository profileLicenseRepository;

    public List<ProfileLicense> getProfileLicenses(final Long memberId) {
        return profileLicenseRepository.getProfileLicenses(memberId);
    }

    public ProfileLicense getProfileLicense(final Long profileLicenseId) {
        return profileLicenseRepository
                .findById(profileLicenseId)
                .orElseThrow(() -> ProfileLicenseNotFoundException.EXCEPTION);
    }

    public boolean existsByProfileId(final Long profileId) {
        return profileLicenseRepository.existsByProfileId(profileId);
    }
}
