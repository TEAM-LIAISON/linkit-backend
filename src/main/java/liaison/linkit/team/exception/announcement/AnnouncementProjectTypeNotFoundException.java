package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementProjectTypeNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementProjectTypeNotFoundException();

    private AnnouncementProjectTypeNotFoundException() {
        super(AnnouncementPositionErrorCode.ANNOUNCEMENT_POSITION_NOT_FOUND);
    }
}
