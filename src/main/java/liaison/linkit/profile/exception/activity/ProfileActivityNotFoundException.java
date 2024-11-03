package liaison.linkit.profile.exception.activity;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileActivityNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileActivityNotFoundException();

    private ProfileActivityNotFoundException() {
        super(ProfileActivityErrorCode.PROFILE_ACTIVITY_NOT_FOUND);
    }
}
