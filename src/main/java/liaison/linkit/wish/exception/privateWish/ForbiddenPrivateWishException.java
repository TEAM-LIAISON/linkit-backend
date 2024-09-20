package liaison.linkit.wish.exception.privateWish;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenPrivateWishException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenPrivateWishException();

    private ForbiddenPrivateWishException() {
        super(PrivateWishErrorCode.FORBIDDEN_PRIVATE_WISH);
    }
}
