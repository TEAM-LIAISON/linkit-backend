package liaison.linkit.team.domain.repository.currentState;

import liaison.linkit.team.domain.state.TeamState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStateRepository
        extends JpaRepository<TeamState, Long>, TeamStateCustomRepository {}
