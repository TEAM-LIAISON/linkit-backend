package liaison.linkit.scrap.exception.teamMemberAnnouncementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberAnnouncementScrapNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberAnnouncementScrapNotFoundException();

    private TeamMemberAnnouncementScrapNotFoundException() {
        super(TeamMemberAnnouncementScrapErrorCode.TEAM_MEMBER_ANNOUNCEMENT_SCRAP_NOT_FOUND);
    }
}
