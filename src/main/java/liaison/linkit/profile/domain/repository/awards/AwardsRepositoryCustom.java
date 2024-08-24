package liaison.linkit.profile.domain.repository.awards;

import liaison.linkit.profile.domain.awards.Awards;

import java.util.List;

public interface AwardsRepositoryCustom {
    boolean existsByProfileId(final Long profileId);
    List<Awards> findAllByProfileId(final Long profileId);

    Awards findByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
}
