package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberForbiddenException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberForbiddenException();

    private TeamMemberForbiddenException() {
        super(TeamMemberErrorCode.TEAM_MEMBER_TYPE_FORBIDDEN_REQUEST);
    }
}
