package liaison.linkit.wish.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamScrapManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamScrapManyRequestException();

    private TeamScrapManyRequestException() {
        super(TeamScrapErrorCode.TOO_MANY_TEAM_SCRAP_REQUEST);
    }
}
