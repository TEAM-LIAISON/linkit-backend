package liaison.linkit.scrap.exception.teamMemberAnnouncementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenTeamMemberAnnouncementScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenTeamMemberAnnouncementScrapException();

    private ForbiddenTeamMemberAnnouncementScrapException() {
        super(TeamMemberAnnouncementScrapErrorCode.FORBIDDEN_TEAM_MEMBER_ANNOUNCEMENT_SCRAP);
    }
}
