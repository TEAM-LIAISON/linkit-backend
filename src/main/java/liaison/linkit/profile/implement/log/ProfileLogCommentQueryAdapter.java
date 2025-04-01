package liaison.linkit.profile.implement.log;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.repository.log.ProfileLogCommentRepository;
import liaison.linkit.profile.exception.log.ProfileLogCommentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * 특정 프로필 로그의 모든 최상위 댓글 목록을 조회합니다.
     *
     * @param profileLogId 프로필 로그 ID
     * @return 최상위 댓글 목록
     */
    public List<ProfileLogComment> getProfileLogComments(final Long profileLogId) {
        // 페이징 없이 모든 댓글 조회
        return profileLogCommentRepository
                .findTopLevelCommentsByProfileLogId(profileLogId, Pageable.unpaged())
                .getContent();
    }

    /**
     * 특정 프로필 로그의 댓글 목록을 페이징하여 조회합니다.
     *
     * @param profileLogId 프로필 로그 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    public Page<ProfileLogComment> getPageProfileLogComments(
            final Long profileLogId, final Pageable pageable) {
        // 페이징하여 댓글 조회
        return profileLogCommentRepository.findTopLevelCommentsByProfileLogId(
                profileLogId, pageable);
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
}
