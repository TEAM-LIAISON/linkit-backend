package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingReceiverBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingReceiverBadRequestException();

    private MatchingReceiverBadRequestException() {
        super(MatchingErrorCode.MATCHING_RECEIVER_BAD_REQUEST);
    }
}
