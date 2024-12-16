package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenProfileScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenProfileScrapException();

    private ForbiddenProfileScrapException() {
        super(ProfileScrapErrorCode.FORBIDDEN_PROFILE_SCRAP);
    }
}
