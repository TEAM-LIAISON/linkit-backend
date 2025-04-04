package liaison.linkit.profile.implement.log;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.repository.log.ProfileLogCommentRepository;
import liaison.linkit.profile.exception.log.ProfileLogCommentNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogCommentQueryAdapter {
    private final ProfileLogCommentRepository profileLogCommentRepository;

    public ProfileLogComment getProfileLogComment(final Long profileLogCommentId) {
        return profileLogCommentRepository
                .findById(profileLogCommentId)
                .orElseThrow(() -> ProfileLogCommentNotFoundException.EXCEPTION);
    }

    /**
     * 특정 부모 댓글에 달린 대댓글 목록을 조회합니다.
     *
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    public List<ProfileLogComment> findRepliesByParentCommentId(final Long parentCommentId) {
        return profileLogCommentRepository.findRepliesByParentCommentId(parentCommentId);
    }

    /**
     * 특정 프로필 로그의 최상위 댓글을 커서 기반으로 조회합니다.
     *
     * @param profileLogId 프로필 로그 ID
     * @param cursorId 커서 ID (마지막으로 조회한 댓글 ID)
     * @param size 조회할 댓글 개수
     * @return 커서 이후의 댓글 목록
     */
    public List<ProfileLogComment> getProfileLogCommentsWithCursor(
            final Long profileLogId, final Long cursorId, final int size) {
        return profileLogCommentRepository.findTopLevelCommentsByProfileLogIdWithCursor(
                profileLogId, cursorId, size);
    }
}
