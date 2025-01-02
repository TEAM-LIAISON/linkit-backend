package liaison.linkit.team.domain.repository.history;

import liaison.linkit.team.domain.history.TeamHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamHistoryRepository extends JpaRepository<TeamHistory, Long>, TeamHistoryCustomRepository {
}
