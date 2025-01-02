package liaison.linkit.team.domain.repository.team;

import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {

}
