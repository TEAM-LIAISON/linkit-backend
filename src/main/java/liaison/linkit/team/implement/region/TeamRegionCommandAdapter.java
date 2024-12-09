package liaison.linkit.team.implement.region;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.TeamRegion;
import liaison.linkit.team.domain.repository.region.TeamRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamRegionCommandAdapter {
    final TeamRegionRepository teamRegionRepository;

    public void save(final TeamRegion teamRegion) {
        teamRegionRepository.save(teamRegion);
    }
}
