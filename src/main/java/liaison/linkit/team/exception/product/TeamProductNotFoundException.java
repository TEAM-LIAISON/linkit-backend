package liaison.linkit.team.exception.product;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamProductNotFoundException extends BaseCodeException {
    public static TeamProductNotFoundException EXCEPTION = new TeamProductNotFoundException();

    private TeamProductNotFoundException() {
        super(TeamProductErrorCode.TEAM_PRODUCT_NOT_FOUND);
    }
}
