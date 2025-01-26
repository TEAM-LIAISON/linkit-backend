package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileScrapBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileScrapBadRequestException();

    private ProfileScrapBadRequestException() {
        super(ProfileScrapErrorCode.PROFILE_SCRAP_BAD_REQUEST);
    }
}
