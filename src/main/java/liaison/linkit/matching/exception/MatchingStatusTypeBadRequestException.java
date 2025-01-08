package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingStatusTypeBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingStatusTypeBadRequestException();

    private MatchingStatusTypeBadRequestException() {
        super(MatchingErrorCode.MATCHING_STATUS_TYPE_BAD_REQUEST);
    }
}
