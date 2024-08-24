package liaison.linkit.profile.domain.repository.region;

import liaison.linkit.profile.domain.region.ProfileRegion;

import java.util.Optional;

public interface ProfileRegionRepositoryCustom {
    Optional<ProfileRegion> findByProfileId(final Long profileId);
    boolean existsByProfileId(final Long profileId);
    void deleteByProfileId(final Long profileId);
}
