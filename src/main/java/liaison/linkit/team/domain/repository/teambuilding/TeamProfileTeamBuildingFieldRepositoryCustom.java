package liaison.linkit.team.domain.repository.teambuilding;

import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;

import java.util.List;

public interface TeamProfileTeamBuildingFieldRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    List<TeamProfileTeamBuildingField> findAllByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
}
