package liaison.linkit.matching.domain.repository.privateMatching;

import liaison.linkit.matching.domain.PrivateMatching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateMatchingRepository extends JpaRepository<PrivateMatching, Long>, PrivateMatchingRepositoryCustom {

}
