package liaison.linkit.profile.exception.region;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileRegionNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileRegionNotFoundException();

    private ProfileRegionNotFoundException() {
        super(ProfileRegionErrorCode.PROFILE_REGION_NOT_FOUND);
    }

}
