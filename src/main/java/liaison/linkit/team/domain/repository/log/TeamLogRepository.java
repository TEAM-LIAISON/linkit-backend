package liaison.linkit.team.domain.repository.log;

import java.util.List;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamLogRepository extends JpaRepository<TeamLog, Long>, TeamLogCustomRepository {

    List<TeamLog> team(Team team);
}
