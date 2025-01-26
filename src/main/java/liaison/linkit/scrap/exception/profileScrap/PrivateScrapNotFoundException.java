package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateScrapNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PrivateScrapNotFoundException();

    private PrivateScrapNotFoundException() {
        super(ProfileScrapErrorCode.PROFILE_SCRAP_NOT_FOUND);
    }
}
