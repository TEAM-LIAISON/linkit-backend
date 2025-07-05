package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CannotRequestMyTeamException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CannotRequestMyTeamException();

    private CannotRequestMyTeamException() {
        super(MatchingErrorCode.CANNOT_REQUEST_MY_TEAM);
    }
}
