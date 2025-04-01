package liaison.linkit.team.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLogComment;
import liaison.linkit.team.domain.repository.log.TeamLogCommentRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogCommentCommandAdapter {
    private final TeamLogCommentRepository teamLogCommentRepository;

    public TeamLogComment addTeamLogComment(final TeamLogComment teamLogComment) {
        return teamLogCommentRepository.save(teamLogComment);
    }
}
