package liaison.linkit.team.implement.region;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.region.TeamRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamRegionQueryAdapter {
    final TeamRegionRepository teamRegionRepository;
}
