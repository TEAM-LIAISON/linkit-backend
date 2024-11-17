package liaison.linkit.common.exception;

public class NotSupportedOauthServiceException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new NotSupportedOauthServiceException();

    private NotSupportedOauthServiceException() {
        super(AuthErrorCode.NOT_SUPPORTED_OAUTH_SERVICE);
    }
}
