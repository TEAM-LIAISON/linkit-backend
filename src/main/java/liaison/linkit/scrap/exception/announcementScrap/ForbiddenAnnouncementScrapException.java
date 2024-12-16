package liaison.linkit.scrap.exception.announcementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenAnnouncementScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenAnnouncementScrapException();

    private ForbiddenAnnouncementScrapException() {
        super(AnnouncementScrapErrorCode.FORBIDDEN_ANNOUNCEMENT_SCRAP);
    }
}
