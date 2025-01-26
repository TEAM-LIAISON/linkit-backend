package liaison.linkit.team.exception.region;

import liaison.linkit.common.exception.BaseCodeException;
import liaison.linkit.team.exception.team.TeamErrorCode;

public class TeamRegionNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamRegionNotFoundException();

    private TeamRegionNotFoundException() {
        super(TeamErrorCode.TEAM_NOT_FOUND);
    }
}
