package liaison.linkit.scrap.domain.repository.privateScrap;

import liaison.linkit.scrap.domain.PrivateScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateScrapRepository extends JpaRepository<PrivateScrap, Long>, PrivateScrapCustomRepository {

}
