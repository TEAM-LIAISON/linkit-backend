package liaison.linkit.team.implement.scale;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamScale.TeamScaleRepository;
import liaison.linkit.team.domain.scale.TeamScale;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScaleCommandAdapter {
    private final TeamScaleRepository teamScaleRepository;

    public void save(final TeamScale teamScale) {
        teamScaleRepository.save(teamScale);
    }
}
