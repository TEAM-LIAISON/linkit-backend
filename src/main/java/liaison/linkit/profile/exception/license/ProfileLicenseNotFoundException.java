package liaison.linkit.profile.exception.license;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileLicenseNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileLicenseNotFoundException();

    private ProfileLicenseNotFoundException() {
        super(ProfileLicenseErrorCode.PROFILE_LICENSE_NOT_FOUND);
    }
}
