package liaison.linkit.team.domain.repository.miniprofile.teamScale;

import liaison.linkit.team.domain.miniprofile.TeamScale;

public interface TeamScaleRepositoryCustom {
    TeamScale findBySizeType(final String sizeType);
}
