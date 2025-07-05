package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementProjectType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementProjectTypeRepository
        extends JpaRepository<AnnouncementProjectType, Long>,
                AnnouncementProjectTypeCustomRepository {}
