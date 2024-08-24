package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;

import java.util.List;

public interface ProfileTeamBuildingFieldRepositoryCustom {
    boolean existsByProfileId(final Long profileId);
    List<ProfileTeamBuildingField> findAllByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
}
