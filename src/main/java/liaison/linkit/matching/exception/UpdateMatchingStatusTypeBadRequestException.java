package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class UpdateMatchingStatusTypeBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new UpdateMatchingStatusTypeBadRequestException();

    private UpdateMatchingStatusTypeBadRequestException() {
        super(MatchingErrorCode.UPDATE_MATCHING_STATUS_TYPE_BAD_REQUEST);
    }
}
