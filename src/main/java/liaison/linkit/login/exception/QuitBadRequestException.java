package liaison.linkit.login.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class QuitBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new QuitBadRequestException();

    private QuitBadRequestException() {
        super(LoginErrorCode.QUIT_BAD_REQUEST);
    }
}
