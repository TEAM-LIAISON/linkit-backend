package liaison.linkit.team.domain.repository.log;

import java.util.List;

import liaison.linkit.team.domain.log.TeamLogComment;

public interface TeamLogCommentCustomRepository {

    /** 특정 팀 로그의 최상위 댓글을 커서 기반으로 조회 */
    List<TeamLogComment> findTopLevelCommentsByTeamLogIdWithCursor(
            Long teamLogId, Long cursorId, int size);

    /** 특정 댓글의 대댓글 목록을 조회 */
    List<TeamLogComment> findRepliesByParentCommentId(Long parentCommentId);
}
