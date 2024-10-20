package liaison.linkit.wish.exception.privateScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenPrivateScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenPrivateScrapException();

    private ForbiddenPrivateScrapException() {
        super(PrivateScrapErrorCode.FORBIDDEN_PRIVATE_SCRAP);
    }
}
