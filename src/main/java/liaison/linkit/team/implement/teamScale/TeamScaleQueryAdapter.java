package liaison.linkit.team.implement.teamScale;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamScale.TeamScaleRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScaleQueryAdapter {
    private final TeamScaleRepository teamScaleRepository;


}
