package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementScrapNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementScrapNotFoundException();

    private AnnouncementScrapNotFoundException() {
        super(AnnouncementScrapErrorCode.ANNOUNCEMENT_SCRAP_NOT_FOUND);
    }
}
