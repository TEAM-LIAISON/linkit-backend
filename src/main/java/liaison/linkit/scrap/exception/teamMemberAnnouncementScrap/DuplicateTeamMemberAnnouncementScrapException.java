package liaison.linkit.scrap.exception.teamMemberAnnouncementScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateTeamMemberAnnouncementScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateTeamMemberAnnouncementScrapException();

    private DuplicateTeamMemberAnnouncementScrapException() {
        super(TeamMemberAnnouncementScrapErrorCode.DUPLICATE_TEAM_MEMBER_ANNOUNCEMENT_SCRAP);
    }
}
