package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberInvitationNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberInvitationNotFoundException();

    private TeamMemberInvitationNotFoundException() {
        super(TeamMemberInvitationErrorCode.TEAM_MEMBER_INVITATION_NOT_FOUND);
    }
}
