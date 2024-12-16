package liaison.linkit.scrap.domain.repository.profileScrap;

import liaison.linkit.scrap.domain.ProfileScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileScrapRepository extends JpaRepository<ProfileScrap, Long>, ProfileScrapCustomRepository {

}
