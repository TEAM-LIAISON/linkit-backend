package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class ParentCommentBadRequestException extends BaseCodeException {
    public static ParentCommentBadRequestException EXCEPTION =
            new ParentCommentBadRequestException();

    private ParentCommentBadRequestException() {
        super(ProfileLogCommentErrorCode.PARENT_COMMENT_BAD_REQUEST);
    }
}
