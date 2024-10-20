package liaison.linkit.scrap.exception.teamMemberAnnouncementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberAnnouncementScrapManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberAnnouncementScrapManyRequestException();

    private TeamMemberAnnouncementScrapManyRequestException() {
        super(TeamMemberAnnouncementScrapErrorCode.TOO_MANY_TEAM_MEMBER_ANNOUNCEMENT_SCRAP_REQUEST);
    }
}
