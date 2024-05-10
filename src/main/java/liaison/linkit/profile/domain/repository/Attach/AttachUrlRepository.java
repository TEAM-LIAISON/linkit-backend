package liaison.linkit.profile.domain.repository.Attach;

import liaison.linkit.profile.domain.Attach.AttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AttachUrlRepository extends JpaRepository<AttachUrl, Long> {
    boolean existsByProfileId(final Long profileId);

    AttachUrl findByProfileId(@Param("profileId") final Long profileId);
}
