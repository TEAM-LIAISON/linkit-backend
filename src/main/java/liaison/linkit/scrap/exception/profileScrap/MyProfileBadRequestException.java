package liaison.linkit.scrap.exception.profileScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class MyProfileBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MyProfileBadRequestException();

    private MyProfileBadRequestException() {
        super(ProfileScrapErrorCode.MY_PROFILE_BAD_REQUEST);
    }
}
