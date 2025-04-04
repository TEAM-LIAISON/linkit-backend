package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateProfileLogCommentBadRequestException extends BaseCodeException {
    public static PrivateProfileLogCommentBadRequestException EXCEPTION =
            new PrivateProfileLogCommentBadRequestException();

    private PrivateProfileLogCommentBadRequestException() {
        super(ProfileLogCommentErrorCode.PRIVATE_PROFILE_LOG_COMMENT_BAD_REQUEST);
    }
}
