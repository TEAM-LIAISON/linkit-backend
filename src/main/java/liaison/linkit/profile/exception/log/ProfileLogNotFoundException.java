package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileLogNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileLogNotFoundException();

    private ProfileLogNotFoundException() {
        super(ProfileLogErrorCode.PROFILE_LOG_NOT_FOUND);
    }
}
