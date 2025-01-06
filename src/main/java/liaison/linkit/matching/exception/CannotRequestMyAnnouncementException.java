package liaison.linkit.matching.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CannotRequestMyAnnouncementException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CannotRequestMyAnnouncementException();

    private CannotRequestMyAnnouncementException() {
        super(MatchingErrorCode.CANNOT_REQUEST_MY_ANNOUNCEMENT);
    }

}
