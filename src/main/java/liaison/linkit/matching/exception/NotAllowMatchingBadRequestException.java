package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class NotAllowMatchingBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new NotAllowMatchingBadRequestException();

    private NotAllowMatchingBadRequestException() {
        super(MatchingErrorCode.NOT_ALLOW_MATCHING_BAD_REQUEST);
    }

}
