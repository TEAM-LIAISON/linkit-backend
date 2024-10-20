package liaison.linkit.wish.exception.privateScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateScrapWishException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateScrapWishException();

    private DuplicateScrapWishException() {
        super(PrivateScrapErrorCode.DUPLICATE_PRIVATE_SCRAP);
    }
}
