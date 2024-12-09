package liaison.linkit.team.implement.scale;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamScale.TeamScaleRepository;
import liaison.linkit.team.domain.scale.TeamScale;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScaleQueryAdapter {
    private final TeamScaleRepository teamScaleRepository;

    public boolean existsTeamScaleByTeamId(final Long teamId) {
        return teamScaleRepository.existsTeamScaleByTeamId(teamId);
    }

    public TeamScale findTeamScaleByTeamId(final Long teamId) {
        return teamScaleRepository.findTeamScaleByTeamId(teamId);
    }
}
