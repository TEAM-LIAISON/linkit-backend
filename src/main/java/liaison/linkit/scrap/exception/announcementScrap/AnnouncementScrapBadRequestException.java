package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementScrapBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementScrapBadRequestException();

    private AnnouncementScrapBadRequestException() {
        super(AnnouncementScrapErrorCode.ANNOUNCEMENT_SCRAP_BAD_REQUEST);
    }
}
