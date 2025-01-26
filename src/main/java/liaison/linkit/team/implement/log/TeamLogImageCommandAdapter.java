package liaison.linkit.team.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.log.TeamLogImage;
import liaison.linkit.team.domain.repository.log.TeamLogImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamLogImageCommandAdapter {

    private final TeamLogImageRepository teamLogImageRepository;

    public void addTeamLogImage(final TeamLogImage teamLogImage) {

        teamLogImageRepository.save(teamLogImage);
    }

    public void removeTeamLogImage(final TeamLogImage teamLogImage) {
        teamLogImageRepository.delete(teamLogImage);
    }
}
