package liaison.linkit.scrap.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class MyTeamBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MyTeamBadRequestException();

    private MyTeamBadRequestException() {
        super(TeamScrapErrorCode.TEAM_SCRAP_BAD_REQUEST);
    }
}
