package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByReceiveMatchingId(final Long profileId);
}
