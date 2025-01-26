package liaison.linkit.team.exception.state;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamStateNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamStateNotFoundException();

    private TeamStateNotFoundException() {
        super(TeamStateErrorCode.TEAM_STATE_NOT_FOUND);
    }
}
