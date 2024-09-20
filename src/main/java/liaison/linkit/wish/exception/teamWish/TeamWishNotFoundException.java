package liaison.linkit.wish.exception.teamWish;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamWishNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamWishNotFoundException();

    private TeamWishNotFoundException() {
        super(TeamWishErrorCode.TEAM_WISH_NOT_FOUND);
    }
}
