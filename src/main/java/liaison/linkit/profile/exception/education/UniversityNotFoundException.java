package liaison.linkit.profile.exception.education;

import liaison.linkit.common.exception.BaseCodeException;

public class UniversityNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new UniversityNotFoundException();

    private UniversityNotFoundException() {
        super(UniversityErrorCode.UNIVERSITY_NOT_FOUND);
    }
}
