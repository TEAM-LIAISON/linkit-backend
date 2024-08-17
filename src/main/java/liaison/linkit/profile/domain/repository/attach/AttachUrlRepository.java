package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttachUrlRepository extends JpaRepository<AttachUrl, Long>, AttachUrlRepositoryCustom{

    Optional<AttachUrl> findByProfileId(@Param("profileId") final Long profileId);
}
