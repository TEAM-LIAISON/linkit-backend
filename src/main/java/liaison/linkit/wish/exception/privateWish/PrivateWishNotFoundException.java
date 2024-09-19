package liaison.linkit.wish.exception.privateWish;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateWishNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PrivateWishNotFoundException();

    private PrivateWishNotFoundException() {
        super(PrivateWishErrorCode.PRIVATE_WISH_NOT_FOUND);
    }
}
