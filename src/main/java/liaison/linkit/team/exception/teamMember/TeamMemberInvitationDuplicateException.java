package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberInvitationDuplicateException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberInvitationDuplicateException();

    private TeamMemberInvitationDuplicateException() {
        super(TeamMemberInvitationErrorCode.DUPLICATE_TEAM_MEMBER_INVITATION_REQUEST);
    }
}
