package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementPositionRepository
        extends JpaRepository<AnnouncementPosition, Long>, AnnouncementPositionCustomRepository {}
