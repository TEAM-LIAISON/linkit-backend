package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingRelationBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingRelationBadRequestException();

    private MatchingRelationBadRequestException() {
        super(MatchingErrorCode.MATCHING_RELATION_BAD_REQUEST);
    }
}
