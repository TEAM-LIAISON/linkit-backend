package liaison.linkit.login.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateEmailRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateEmailRequestException();

    private DuplicateEmailRequestException() {
        super(LoginErrorCode.DUPLICATE_EMAIL_REQUEST);
    }
}
