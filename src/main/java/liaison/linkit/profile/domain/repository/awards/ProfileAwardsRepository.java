package liaison.linkit.profile.domain.repository.awards;

import java.util.List;
import liaison.linkit.profile.domain.ProfileAwards;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileAwardsRepository extends JpaRepository<ProfileAwards, Long>, ProfileAwardsCustomRepository {

}
