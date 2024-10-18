package liaison.linkit.profile.domain.repository.awards;

import liaison.linkit.profile.domain.ProfileAwards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardsRepository extends JpaRepository<ProfileAwards, Long>, AwardsRepositoryCustom {

}
