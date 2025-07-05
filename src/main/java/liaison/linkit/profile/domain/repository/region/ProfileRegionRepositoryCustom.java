package liaison.linkit.profile.domain.repository.region;

import java.util.Optional;

import liaison.linkit.profile.domain.region.ProfileRegion;

public interface ProfileRegionRepositoryCustom {
    Optional<ProfileRegion> findProfileRegionByProfileId(final Long profileId);

    boolean existsProfileRegionByProfileId(final Long profileId);

    void deleteByProfileId(final Long profileId);
}
