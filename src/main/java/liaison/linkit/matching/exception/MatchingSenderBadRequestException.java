package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingSenderBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingSenderBadRequestException();

    private MatchingSenderBadRequestException() {
        super(MatchingErrorCode.MATCHING_SENDER_BAD_REQUEST);
    }
}
