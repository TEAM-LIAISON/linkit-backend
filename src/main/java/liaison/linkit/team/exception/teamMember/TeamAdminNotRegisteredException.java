package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamAdminNotRegisteredException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamAdminNotRegisteredException();

    private TeamAdminNotRegisteredException() {
        super(TeamMemberErrorCode.TEAM_ADMIN_NOT_REGISTERED_BAD_REQUEST);
    }
}
