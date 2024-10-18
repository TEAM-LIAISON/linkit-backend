package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.ProfileLicenseRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLicenseCommandAdapter {
    private final ProfileLicenseRepository profileLicenseRepository;

    public void deleteProfileLicenseById(final Long profileLicenseId) {
        profileLicenseRepository.deleteById(profileLicenseId);
    }
}
