package liaison.linkit.team.exception.announcement;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberAnnouncementNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberAnnouncementNotFoundException();

    private TeamMemberAnnouncementNotFoundException() {
        super(TeamMemberAnnouncementErrorCode.TEAM_MEMBER_ANNOUNCEMENT_NOT_FOUND);
    }
}
