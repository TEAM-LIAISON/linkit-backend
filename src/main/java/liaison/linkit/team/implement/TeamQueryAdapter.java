package liaison.linkit.team.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.exception.TeamNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamQueryAdapter {

    private final TeamRepository teamRepository;

    public Team findById(final Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> TeamNotFoundException.EXCEPTION);
    }
}
