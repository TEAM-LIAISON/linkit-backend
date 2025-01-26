package liaison.linkit.team.exception.teamMember;

import liaison.linkit.common.exception.BaseCodeException;

public class ManagingBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ManagingBadRequestException();

    private ManagingBadRequestException() {
        super(TeamMemberErrorCode.MANAGING_BAD_REQUEST);
    }

}
