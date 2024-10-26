package liaison.linkit.scrap.exception.privateScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicatePrivateScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicatePrivateScrapException();

    private DuplicatePrivateScrapException() {
        super(PrivateScrapErrorCode.DUPLICATE_PRIVATE_SCRAP);
    }
}
