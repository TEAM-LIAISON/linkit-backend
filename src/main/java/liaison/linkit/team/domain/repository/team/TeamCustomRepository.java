package liaison.linkit.team.domain.repository.team;

import java.util.List;
import java.util.Optional;
import liaison.linkit.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamCustomRepository {
    Optional<Team> findByTeamName(final String teamName);

    boolean existsByTeamCode(final String teamCode);

    Page<Team> findAllByFiltering(
            final List<String> scaleName,
            final Boolean isAnnouncement,
            final List<String> cityName,
            final List<String> teamStateName,
            final Pageable pageable
    );
}
