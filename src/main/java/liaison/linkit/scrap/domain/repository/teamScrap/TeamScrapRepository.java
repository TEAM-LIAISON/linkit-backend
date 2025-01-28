package liaison.linkit.scrap.domain.repository.teamScrap;

import java.util.List;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamScrapRepository extends JpaRepository<TeamScrap, Long>, TeamScrapCustomRepository {
    List<TeamScrap> team(Team team);
}
