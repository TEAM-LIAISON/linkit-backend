package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class ParentCommentBadRequestException extends BaseCodeException {
    public static ParentCommentBadRequestException EXCEPTION =
            new ParentCommentBadRequestException();

    private ParentCommentBadRequestException() {
        super(TeamLogCommentErrorCode.PARENT_COMMENT_BAD_REQUEST);
    }
}
