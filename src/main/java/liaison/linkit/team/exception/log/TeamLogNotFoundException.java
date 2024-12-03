package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamLogNotFoundException extends BaseCodeException {
    public static TeamLogNotFoundException EXCEPTION = new TeamLogNotFoundException();

    private TeamLogNotFoundException() {
        super(TeamLogErrorCode.TEAM_LOG_NOT_FOUND);
    }

}
