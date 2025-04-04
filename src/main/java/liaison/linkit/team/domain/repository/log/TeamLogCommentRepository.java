package liaison.linkit.team.domain.repository.log;

import liaison.linkit.team.domain.log.TeamLogComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamLogCommentRepository
        extends JpaRepository<TeamLogComment, Long>, TeamLogCommentCustomRepository {}
