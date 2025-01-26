package liaison.linkit.profile.domain.repository.license;


import java.util.List;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.UpdateProfileLicenseRequest;

public interface ProfileLicenseCustomRepository {
    List<ProfileLicense> getProfileLicenses(final Long memberId);

    ProfileLicense updateProfileLicense(final Long profileLicenseId, final UpdateProfileLicenseRequest updateProfileLicenseRequest);

    boolean existsByProfileId(final Long profileId);
}
