package liaison.linkit.profile.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.repository.log.ProfileLogCommentRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogCommentCommandAdapter {
    private final ProfileLogCommentRepository profileLogCommentRepository;

    public ProfileLogComment addProfileLogComment(final ProfileLogComment profileLogComment) {
        return profileLogCommentRepository.save(profileLogComment);
    }
}
