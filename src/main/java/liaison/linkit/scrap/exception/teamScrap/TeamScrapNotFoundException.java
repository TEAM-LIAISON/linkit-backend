package liaison.linkit.scrap.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamScrapNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamScrapNotFoundException();

    private TeamScrapNotFoundException() {
        super(TeamScrapErrorCode.TEAM_SCRAP_NOT_FOUND);
    }
}
