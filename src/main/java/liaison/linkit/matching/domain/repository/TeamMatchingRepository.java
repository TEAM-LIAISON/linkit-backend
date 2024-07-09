package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.TeamMatching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {
}
