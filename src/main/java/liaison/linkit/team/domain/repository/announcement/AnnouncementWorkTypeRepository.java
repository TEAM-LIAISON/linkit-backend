package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementWorkType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementWorkTypeRepository
        extends JpaRepository<AnnouncementWorkType, Long>, AnnouncementWorkTypeCustomRepository {}
