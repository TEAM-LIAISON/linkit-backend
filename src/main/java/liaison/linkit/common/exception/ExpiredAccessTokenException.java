package liaison.linkit.common.exception;

public class ExpiredAccessTokenException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new ExpiredAccessTokenException();

    private ExpiredAccessTokenException() {
        super(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
    }
}
