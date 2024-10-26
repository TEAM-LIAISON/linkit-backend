package liaison.linkit.team.implement.teamScale;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamScale.TeamScaleRepository;
import liaison.linkit.team.domain.scale.TeamScale;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScaleQueryAdapter {
    private final TeamScaleRepository teamScaleRepository;

    public TeamScale findByScaleName(final String scaleName) {
        return teamScaleRepository.findByScaleName(scaleName);
    }

}
