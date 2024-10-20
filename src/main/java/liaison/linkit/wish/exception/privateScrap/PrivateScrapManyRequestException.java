package liaison.linkit.wish.exception.privateScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateScrapManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PrivateScrapManyRequestException();

    private PrivateScrapManyRequestException() {
        super(PrivateScrapErrorCode.TOO_MANY_PRIVATE_SCRAP_REQUEST);
    }
}
