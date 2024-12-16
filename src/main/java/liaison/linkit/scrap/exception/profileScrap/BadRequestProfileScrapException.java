package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class BadRequestProfileScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new BadRequestProfileScrapException();

    private BadRequestProfileScrapException() {
        super(ProfileScrapErrorCode.PROFILE_SCRAP_BAD_REQUEST);
    }
}
