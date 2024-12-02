package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementPositionNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementPositionNotFoundException();

    private AnnouncementPositionNotFoundException() {
        super(AnnouncementPositionErrorCode.ANNOUNCEMENT_POSITION_NOT_FOUND);
    }
}
