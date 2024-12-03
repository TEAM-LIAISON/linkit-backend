package liaison.linkit.team.domain.repository.currentState;

import liaison.linkit.team.domain.TeamCurrentState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamCurrentStateRepository extends JpaRepository<TeamCurrentState, Long>, TeamCurrentStateCustomRepository {
    
}
