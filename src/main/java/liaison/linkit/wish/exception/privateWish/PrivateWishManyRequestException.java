package liaison.linkit.wish.exception.privateWish;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateWishManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PrivateWishManyRequestException();

    private PrivateWishManyRequestException() {
        super(PrivateWishErrorCode.TOO_MANY_PRIVATE_WISH_REQUEST);
    }
}
