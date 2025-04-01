package liaison.linkit.team.business.service.log;

import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.team.business.mapper.log.TeamLogCommentMapper;
import liaison.linkit.team.implement.log.TeamLogCommentCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogCommentQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamLogCommentService {

    // Adapters
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamLogQueryAdapter teamLogQueryAdapter;
    private final TeamLogCommentQueryAdapter teamLogCommentQueryAdapter;
    private final TeamLogCommentCommandAdapter teamLogCommentCommandAdapter;

    // Mappers
    private final TeamLogCommentMapper teamLogCommentMapper;
}
