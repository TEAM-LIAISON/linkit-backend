package liaison.linkit.common.exception;

public class InvalidRefreshTokenException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidRefreshTokenException();

    private InvalidRefreshTokenException() {
        super(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }
}
