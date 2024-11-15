package liaison.linkit.common.exception;

public class InvalidTokenException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidTokenException();

    private InvalidTokenException() {
        super(AuthErrorCode.INVALID_TOKEN);
    }
}
