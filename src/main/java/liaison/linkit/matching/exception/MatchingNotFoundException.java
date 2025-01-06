package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingNotFoundException();

    private MatchingNotFoundException() {
        super(MatchingErrorCode.MATCHING_NOT_FOUND);
    }
}
