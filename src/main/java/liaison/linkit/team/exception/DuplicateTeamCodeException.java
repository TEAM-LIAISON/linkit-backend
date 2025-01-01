package liaison.linkit.team.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateTeamCodeException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateTeamCodeException();

    private DuplicateTeamCodeException() {
        super(TeamErrorCode.DUPLICATE_TEAM_CODE);
    }
}
