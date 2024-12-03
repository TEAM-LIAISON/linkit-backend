package liaison.linkit.team.domain.repository.log;

import liaison.linkit.team.domain.log.TeamLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamLogRepository extends JpaRepository<TeamLog, Long>, TeamLogCustomRepository {
    
}
