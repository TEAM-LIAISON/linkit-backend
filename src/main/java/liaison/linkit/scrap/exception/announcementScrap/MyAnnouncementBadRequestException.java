package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class MyAnnouncementBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MyAnnouncementBadRequestException();

    private MyAnnouncementBadRequestException() {
        super(AnnouncementScrapErrorCode.MY_ANNOUNCEMENT_BAD_REQUEST);

    }
}
