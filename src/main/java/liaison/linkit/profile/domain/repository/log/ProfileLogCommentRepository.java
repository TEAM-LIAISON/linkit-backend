package liaison.linkit.profile.domain.repository.log;

import liaison.linkit.profile.domain.log.ProfileLogComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLogCommentRepository
        extends JpaRepository<ProfileLogComment, Long>, ProfileLogCommentCustomRepository {}
