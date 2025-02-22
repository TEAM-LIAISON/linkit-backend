package liaison.linkit.profile.domain.repository.awards;

import liaison.linkit.profile.domain.awards.ProfileAwards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileAwardsRepository
        extends JpaRepository<ProfileAwards, Long>, ProfileAwardsCustomRepository {}
