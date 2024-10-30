package liaison.linkit.login.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class LoginBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new LoginBadRequestException();

    private LoginBadRequestException() {
        super(LoginErrorCode.AUTH_CODE_BAD_REQUEST);
    }
}
