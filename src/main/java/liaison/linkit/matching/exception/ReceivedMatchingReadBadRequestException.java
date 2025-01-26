package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class ReceivedMatchingReadBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ReceivedMatchingReadBadRequestException();

    private ReceivedMatchingReadBadRequestException() {
        super(MatchingErrorCode.RECEIVED_MATCHING_READ_BAD_REQUEST);
    }
}
