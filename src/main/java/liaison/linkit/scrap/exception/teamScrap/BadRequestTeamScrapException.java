package liaison.linkit.scrap.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class BadRequestTeamScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new BadRequestTeamScrapException();

    private BadRequestTeamScrapException() {
        super(TeamScrapErrorCode.TEAM_SCRAP_BAD_REQUEST);
    }
}
