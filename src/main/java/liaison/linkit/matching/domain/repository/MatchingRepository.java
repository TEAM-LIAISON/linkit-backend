package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long>, MatchingCustomRepository {
}
