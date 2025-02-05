package liaison.linkit.profile.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class UpdateProfileLogPublicBadRequestException extends BaseCodeException {
    public static UpdateProfileLogPublicBadRequestException EXCEPTION = new UpdateProfileLogPublicBadRequestException();

    private UpdateProfileLogPublicBadRequestException() {
        super(ProfileLogErrorCode.UPDATE_PROFILE_LOG_PUBLIC_BAD_REQUEST);
    }
}
