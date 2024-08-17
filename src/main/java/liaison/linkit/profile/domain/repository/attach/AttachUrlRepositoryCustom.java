package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;

import java.util.List;
import java.util.Optional;

public interface AttachUrlRepositoryCustom {
    Optional<AttachUrl> findByProfileId(final Long profileId);
    boolean existsByProfileId(final Long profileId);
    List<AttachUrl> findAllByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
}
