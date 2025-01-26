package liaison.linkit.scrap.domain.repository.teamScrap;

import liaison.linkit.scrap.domain.TeamScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamScrapRepository extends JpaRepository<TeamScrap, Long>, TeamScrapCustomRepository {
}
