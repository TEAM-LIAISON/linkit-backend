package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementWorkTypeNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementWorkTypeNotFoundException();

    private AnnouncementWorkTypeNotFoundException() {
        super(AnnouncementWorkTypeErrorCode.ANNOUNCEMENT_WORK_TYPE_NOT_FOUND);
    }
}
