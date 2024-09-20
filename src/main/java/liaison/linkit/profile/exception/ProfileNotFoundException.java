package liaison.linkit.profile.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileNotFoundException();

    private ProfileNotFoundException() {
        super(ProfileErrorCode.PROFILE_NOT_FOUND);
    }
}
