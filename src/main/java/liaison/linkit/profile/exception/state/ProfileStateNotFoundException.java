package liaison.linkit.profile.exception.state;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileStateNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileStateNotFoundException();

    private ProfileStateNotFoundException() {
        super(ProfileStateErrorCode.PROFILE_STATE_NOT_FOUND);
    }

}
