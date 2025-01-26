package liaison.linkit.profile.domain.repository.position;

import liaison.linkit.profile.domain.position.ProfilePosition;

public interface ProfilePositionCustomRepository {

    boolean existsProfilePositionByProfileId(final Long profileId);

    ProfilePosition findProfilePositionByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
