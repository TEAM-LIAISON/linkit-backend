package liaison.linkit.scrap.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenTeamScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenTeamScrapException();

    private ForbiddenTeamScrapException() {
        super(TeamScrapErrorCode.FORBIDDEN_TEAM_SCRAP);
    }
}
