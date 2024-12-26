package liaison.linkit.matching.domain.repository.profileMatching;

import liaison.linkit.matching.domain.ProfileMatching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileMatchingRepository extends JpaRepository<ProfileMatching, Long>, ProfileMatchingRepositoryCustom {

}
