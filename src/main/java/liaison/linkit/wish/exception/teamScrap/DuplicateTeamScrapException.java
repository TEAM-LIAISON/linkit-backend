package liaison.linkit.wish.exception.teamScrap;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateTeamScrapException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateTeamScrapException();

    private DuplicateTeamScrapException() {
        super(TeamScrapErrorCode.DUPLICATE_TEAM_SCRAP);
    }
}
