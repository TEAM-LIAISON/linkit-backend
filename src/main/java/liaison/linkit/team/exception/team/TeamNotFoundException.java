package liaison.linkit.team.exception.team;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamNotFoundException();

    private TeamNotFoundException() {
        super(TeamErrorCode.TEAM_NOT_FOUND);
    }
}
