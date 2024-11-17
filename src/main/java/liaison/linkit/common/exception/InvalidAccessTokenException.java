package liaison.linkit.common.exception;

public class InvalidAccessTokenException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidAccessTokenException();

    private InvalidAccessTokenException() {
        super(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }
}
