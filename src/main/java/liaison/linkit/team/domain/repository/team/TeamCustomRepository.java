package liaison.linkit.team.domain.repository.team;

import java.util.Optional;
import liaison.linkit.team.domain.Team;

public interface TeamCustomRepository {
    Optional<Team> findByTeamName(final String teamName);
}
