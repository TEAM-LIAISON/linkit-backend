package liaison.linkit.team.implement.log;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLogComment;
import liaison.linkit.team.domain.repository.log.TeamLogCommentRepository;
import liaison.linkit.team.exception.log.TeamLogCommentNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogCommentQueryAdapter {
    private final TeamLogCommentRepository teamLogCommentRepository;

    public TeamLogComment getTeamLogComment(final Long teamLogCommentId) {
        return teamLogCommentRepository
                .findById(teamLogCommentId)
                .orElseThrow(() -> TeamLogCommentNotFoundException.EXCEPTION);
    }

    /**
     * 특정 부모 댓글에 달린 대댓글 목록을 조회합니다.
     *
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    public List<TeamLogComment> findRepliesByParentCommentId(final Long parentCommentId) {
        return teamLogCommentRepository.findRepliesByParentCommentId(parentCommentId);
    }

    /**
     * 특정 팀 로그의 최상위 댓글을 커서 기반으로 조회합니다.
     *
     * @param teamLogId 프로필 로그 ID
     * @param cursorId 커서 ID (마지막으로 조회한 댓글 ID)
     * @param size 조회할 댓글 개수
     * @return 커서 이후의 댓글 목록
     */
    public List<TeamLogComment> getTeamLogCommentsWithCursor(
            final Long teamLogId, final Long cursorId, final int size) {
        return teamLogCommentRepository.findTopLevelCommentsByTeamLogIdWithCursor(
                teamLogId, cursorId, size);
    }
}
