package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.Attach.AttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttachUrlRepository extends JpaRepository<AttachUrl, Long> {

    boolean existsByProfileId(final Long profileId);
    AttachUrl findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT attachUrl FROM AttachUrl attachUrl WHERE attachUrl.profile.id = :profileId")
    List<AttachUrl> findAllByProfileId(@Param("profileId") final Long profileId);
}
