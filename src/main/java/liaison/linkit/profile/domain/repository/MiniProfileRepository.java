package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.MiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long> {
    boolean existsByProfileId(final Long profileId);

    Optional<MiniProfile> findByProfileId(@Param("profileId") final Long profileId);

    void deleteByProfileId(Long id);
}
