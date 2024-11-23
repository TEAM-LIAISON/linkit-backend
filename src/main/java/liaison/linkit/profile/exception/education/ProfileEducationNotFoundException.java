package liaison.linkit.profile.exception.education;

import liaison.linkit.common.exception.BaseCodeException;

public class ProfileEducationNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ProfileEducationNotFoundException();

    private ProfileEducationNotFoundException() {
        super(ProfileEducationErrorCode.PROFILE_EDUCATION_NOT_FOUND);
    }
}
