package liaison.linkit.profile.domain.repository.awards;

import liaison.linkit.profile.domain.ProfileAwards;

import java.util.List;

public interface AwardsRepositoryCustom {
    boolean existsByProfileId(final Long profileId);

    List<ProfileAwards> findAllByProfileId(final Long profileId);

    ProfileAwards findByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
