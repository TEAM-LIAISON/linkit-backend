package liaison.linkit.team.exception.history;

import liaison.linkit.common.exception.BaseCodeException;
import liaison.linkit.team.exception.TeamErrorCode;

public class TeamHistoryNotFoundException extends BaseCodeException {
    public static TeamHistoryNotFoundException EXCEPTION = new TeamHistoryNotFoundException();

    private TeamHistoryNotFoundException() {
        super(TeamErrorCode.TEAM_NOT_FOUND);
    }
}
