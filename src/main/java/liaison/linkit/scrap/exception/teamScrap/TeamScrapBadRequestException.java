package liaison.linkit.scrap.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamScrapBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamScrapBadRequestException();

    private TeamScrapBadRequestException() {
        super(TeamScrapErrorCode.TEAM_SCRAP_BAD_REQUEST);
    }
}
