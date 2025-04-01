package liaison.linkit.profile.domain.repository.log;

import java.util.List;

import liaison.linkit.profile.domain.log.ProfileLogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileLogCommentCustomRepository {

    /** 특정 프로필 로그의 최상위 댓글을 페이징하여 조회 */
    Page<ProfileLogComment> findTopLevelCommentsByProfileLogId(
            Long profileLogId, Pageable pageable);

    /** 특정 프로필 로그의 최상위 댓글을 커서 기반으로 조회 */
    List<ProfileLogComment> findTopLevelCommentsByProfileLogIdWithCursor(
            Long profileLogId, Long cursorId, int size);

    /** 특정 댓글의 대댓글 목록을 조회 */
    List<ProfileLogComment> findRepliesByParentCommentId(Long parentCommentId);

    /** 특정 프로필이 작성한 댓글 목록을 페이징하여 조회 */
    Page<ProfileLogComment> findCommentsByProfileId(Long profileId, Pageable pageable);

    /** 특정 프로필 로그의 댓글 수 조회 */
    Long countCommentsByProfileLogId(Long profileLogId);
}
