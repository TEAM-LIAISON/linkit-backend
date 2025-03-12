package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class UpdateTeamLogPublicBadRequestException extends BaseCodeException {
    public static UpdateTeamLogPublicBadRequestException EXCEPTION =
            new UpdateTeamLogPublicBadRequestException();

    private UpdateTeamLogPublicBadRequestException() {
        super(TeamLogErrorCode.UPDATE_TEAM_LOG_PUBLIC_BAD_REQUEST);
    }
}
