package liaison.linkit.wish.exception.teamWish;

import liaison.linkit.common.exception.BaseCodeException;

public class ForbiddenTeamWishException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ForbiddenTeamWishException();

    private ForbiddenTeamWishException() {
        super(TeamWishErrorCode.FORBIDDEN_TEAM_WISH);
    }
}
