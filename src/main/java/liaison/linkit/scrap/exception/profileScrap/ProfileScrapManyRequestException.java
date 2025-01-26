package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileScrapManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileScrapManyRequestException();

    private ProfileScrapManyRequestException() {
        super(ProfileScrapErrorCode.TOO_MANY_PROFILE_SCRAP_REQUEST);
    }
}
