package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementScrapManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementScrapManyRequestException();

    private AnnouncementScrapManyRequestException() {
        super(AnnouncementScrapErrorCode.TOO_MANY_ANNOUNCEMENT_SCRAP_REQUEST);
    }
}
