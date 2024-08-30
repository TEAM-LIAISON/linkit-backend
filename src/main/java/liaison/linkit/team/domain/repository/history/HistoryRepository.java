package liaison.linkit.team.domain.repository.history;

import liaison.linkit.team.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}
