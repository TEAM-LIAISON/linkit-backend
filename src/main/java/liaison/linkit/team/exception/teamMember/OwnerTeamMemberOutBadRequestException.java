package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class OwnerTeamMemberOutBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new OwnerTeamMemberOutBadRequestException();

    private OwnerTeamMemberOutBadRequestException() {
        super(TeamMemberErrorCode.OWNER_TEAM_MEMBER_OUT_BAD_REQUEST);
    }
}
