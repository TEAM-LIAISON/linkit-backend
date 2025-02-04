package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class UpdateTeamLogTypeBadRequestException extends BaseCodeException {
    public static UpdateTeamLogTypeBadRequestException EXCEPTION = new UpdateTeamLogTypeBadRequestException();

    private UpdateTeamLogTypeBadRequestException() {
        super(TeamLogErrorCode.UPDATE_TEAM_LOG_TYPE_BAD_REQUEST);
    }
}
