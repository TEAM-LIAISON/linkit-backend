package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class UpdateProfileLogTypeBadRequestException extends BaseCodeException {
    public static UpdateProfileLogTypeBadRequestException EXCEPTION =
            new UpdateProfileLogTypeBadRequestException();

    private UpdateProfileLogTypeBadRequestException() {
        super(ProfileLogErrorCode.UPDATE_PROFILE_LOG_TYPE_BAD_REQUEST);
    }
}
