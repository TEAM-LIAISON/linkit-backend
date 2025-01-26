package liaison.linkit.login.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class AuthCodeBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AuthCodeBadRequestException();

    private AuthCodeBadRequestException() {
        super(LoginErrorCode.AUTH_CODE_BAD_REQUEST);
    }
}
