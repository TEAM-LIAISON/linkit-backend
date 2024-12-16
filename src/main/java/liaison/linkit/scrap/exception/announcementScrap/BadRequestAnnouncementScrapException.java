package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class BadRequestAnnouncementScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new BadRequestAnnouncementScrapException();

    private BadRequestAnnouncementScrapException() {
        super(AnnouncementScrapErrorCode.ANNOUNCEMENT_SCRAP_BAD_REQUEST);
    }
}
