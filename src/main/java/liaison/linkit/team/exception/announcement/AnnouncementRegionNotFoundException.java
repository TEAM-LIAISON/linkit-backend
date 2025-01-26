package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class AnnouncementRegionNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new AnnouncementRegionNotFoundException();

    private AnnouncementRegionNotFoundException() {
        super(AnnouncementRegionErrorCode.ANNOUNCEMENT_REGION_NOT_FOUND);
    }
}
