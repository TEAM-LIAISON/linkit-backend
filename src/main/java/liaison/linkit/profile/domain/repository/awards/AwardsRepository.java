package liaison.linkit.profile.domain.repository.awards;

import liaison.linkit.profile.domain.awards.Awards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardsRepository extends JpaRepository<Awards, Long>, AwardsRepositoryCustom {

}
