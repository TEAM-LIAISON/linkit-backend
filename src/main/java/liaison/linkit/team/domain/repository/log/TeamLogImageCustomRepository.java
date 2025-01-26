package liaison.linkit.team.domain.repository.log;

import java.util.List;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.log.TeamLogImage;

public interface TeamLogImageCustomRepository {
    List<TeamLogImage> findByTeamLog(final TeamLog teamLog);
}
