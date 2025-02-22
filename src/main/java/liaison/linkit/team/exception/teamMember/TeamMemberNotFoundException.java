package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamMemberNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamMemberNotFoundException();

    private TeamMemberNotFoundException() {
        super(TeamMemberErrorCode.TEAM_MEMBER_NOT_FOUND);
    }
}
