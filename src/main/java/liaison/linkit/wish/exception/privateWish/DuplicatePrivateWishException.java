package liaison.linkit.wish.exception.privateWish;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicatePrivateWishException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicatePrivateWishException();

    private DuplicatePrivateWishException() {
        super(PrivateWishErrorCode.DUPLICATE_PRIVATE_WISH);
    }
}
