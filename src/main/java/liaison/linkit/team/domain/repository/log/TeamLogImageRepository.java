package liaison.linkit.team.domain.repository.log;

import liaison.linkit.team.domain.log.TeamLogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamLogImageRepository
        extends JpaRepository<TeamLogImage, Long>, TeamLogImageCustomRepository {}
