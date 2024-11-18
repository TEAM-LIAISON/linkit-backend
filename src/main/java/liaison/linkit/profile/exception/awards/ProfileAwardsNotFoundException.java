package liaison.linkit.profile.exception.awards;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileAwardsNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileAwardsNotFoundException();

    private ProfileAwardsNotFoundException() {
        super(ProfileAwardsErrorCode.PROFILE_AWARDS_NOT_FOUND);
    }
}
