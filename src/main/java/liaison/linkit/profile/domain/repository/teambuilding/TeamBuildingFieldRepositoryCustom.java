package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;

import java.util.List;

public interface TeamBuildingFieldRepositoryCustom {
    List<TeamBuildingField> findTeamBuildingFieldsByFieldNames(final List<String> teamBuildingFieldNames);
}
