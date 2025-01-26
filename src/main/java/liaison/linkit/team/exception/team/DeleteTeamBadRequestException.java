package liaison.linkit.team.exception.team;

import liaison.linkit.common.exception.BaseCodeException;

public class DeleteTeamBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DeleteTeamBadRequestException();

    private DeleteTeamBadRequestException() {
        super(TeamErrorCode.DELETE_TEAM_BAD_REQUEST);
    }
}
