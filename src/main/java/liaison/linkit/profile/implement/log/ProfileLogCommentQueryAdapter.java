package liaison.linkit.profile.implement.log;

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
}
