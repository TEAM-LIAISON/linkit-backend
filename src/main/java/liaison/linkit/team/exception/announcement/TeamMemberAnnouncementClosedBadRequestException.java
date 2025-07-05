package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberAnnouncementClosedBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION =
            new TeamMemberAnnouncementClosedBadRequestException();

    private TeamMemberAnnouncementClosedBadRequestException() {
        super(TeamMemberAnnouncementErrorCode.ANNOUNCEMENT_CLOSED_BAD_REQUEST);
    }
}
