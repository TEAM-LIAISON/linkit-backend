package liaison.linkit.team.domain.repository.history;

import liaison.linkit.team.domain.TeamHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamHistoryRepository extends JpaRepository<TeamHistory, Long>, TeamHistoryCustomRepository {
}
