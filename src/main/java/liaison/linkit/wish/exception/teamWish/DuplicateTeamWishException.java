package liaison.linkit.wish.exception.teamWish;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateTeamWishException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateTeamWishException();

    private DuplicateTeamWishException() {
        super(TeamWishErrorCode.DUPLICATE_TEAM_WISH);
    }
}
