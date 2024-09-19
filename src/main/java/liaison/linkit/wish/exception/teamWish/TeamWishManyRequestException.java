package liaison.linkit.wish.exception.teamWish;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamWishManyRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new TeamWishManyRequestException();

    private TeamWishManyRequestException() {
        super(TeamWishErrorCode.TOO_MANY_TEAM_WISH_REQUEST);
    }
}
