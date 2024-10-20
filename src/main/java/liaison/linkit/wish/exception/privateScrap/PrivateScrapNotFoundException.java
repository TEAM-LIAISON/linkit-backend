package liaison.linkit.wish.exception.privateScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateScrapNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PrivateScrapNotFoundException();

    private PrivateScrapNotFoundException() {
        super(PrivateScrapErrorCode.PRIVATE_SCRAP_NOT_FOUND);
    }
}
