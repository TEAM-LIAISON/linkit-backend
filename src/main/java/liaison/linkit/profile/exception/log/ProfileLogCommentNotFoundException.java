package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileLogCommentNotFoundException extends BaseCodeException {
    public static ProfileLogCommentNotFoundException EXCEPTION =
            new ProfileLogCommentNotFoundException();

    private ProfileLogCommentNotFoundException() {
        super(ProfileLogCommentErrorCode.PROFILE_LOG_COMMENT_NOT_FOUND);
    }
}
