package liaison.linkit.team.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamCommandAdapter {
    private final TeamRepository teamRepository;

    public Team add(final Team team) {
        return teamRepository.save(team);
    }
}
