package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class PrivateTeamLogCommentBadRequestException extends BaseCodeException {
    public static PrivateTeamLogCommentBadRequestException EXCEPTION =
            new PrivateTeamLogCommentBadRequestException();

    private PrivateTeamLogCommentBadRequestException() {
        super(TeamLogCommentErrorCode.PRIVATE_TEAM_LOG_COMMENT_BAD_REQUEST);
    }
}
