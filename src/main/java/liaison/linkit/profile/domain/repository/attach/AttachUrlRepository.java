package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachUrlRepository extends JpaRepository<AttachUrl, Long> {

    boolean existsByProfileId(final Long profileId);

    Optional<AttachUrl> findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT attachUrl FROM AttachUrl attachUrl WHERE attachUrl.profile.id = :profileId")
    List<AttachUrl> findAllByProfileId(@Param("profileId") final Long profileId);
}
