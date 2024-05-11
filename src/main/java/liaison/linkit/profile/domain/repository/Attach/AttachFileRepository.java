package liaison.linkit.profile.domain.repository.Attach;

import liaison.linkit.profile.domain.Attach.AttachFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {
    boolean existsByProfileId(final Long profileId);

    AttachFile findByProfileId(@Param("profileId") final Long profileId);

}
