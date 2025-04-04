package liaison.linkit.team.exception.log;

import liaison.linkit.common.exception.BaseCodeException;

public class TeamLogCommentNotFoundException extends BaseCodeException {
    public static TeamLogCommentNotFoundException EXCEPTION = new TeamLogCommentNotFoundException();

    private TeamLogCommentNotFoundException() {
        super(TeamLogCommentErrorCode.TEAM_LOG_COMMENT_NOT_FOUND);
    }
}
