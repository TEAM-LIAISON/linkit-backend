package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class RemoveTeamMemberBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new RemoveTeamMemberBadRequestException();

    private RemoveTeamMemberBadRequestException() {
        super(TeamMemberErrorCode.REMOVE_TEAM_MEMBER_BAD_REQUEST);
    }

}
