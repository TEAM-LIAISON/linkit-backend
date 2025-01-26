package liaison.linkit.team.implement.log;


import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.log.TeamLogImage;
import liaison.linkit.team.domain.repository.log.TeamLogImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogImageQueryAdapter {
    private final TeamLogImageRepository teamLogImageRepository;

    public List<TeamLogImage> findByTeamLog(final TeamLog teamLog) {
        return teamLogImageRepository.findByTeamLog(teamLog);
    }
}
