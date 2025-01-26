package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CompletedMatchingReadBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CompletedMatchingReadBadRequestException();

    private CompletedMatchingReadBadRequestException() {
        super(MatchingErrorCode.COMPLETED_MATCHING_READ_BAD_REQUEST);
    }
}
