package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CannotRequestMyProfileException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CannotRequestMyProfileException();

    private CannotRequestMyProfileException() {
        super(MatchingErrorCode.CANNOT_REQUEST_MY_PROFILE);
    }

}
