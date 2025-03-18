package liaison.linkit.team.domain.repository.announcement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementProjectTypeRepository
        extends JpaRepository<AnnouncementSkillRepository, Long>,
                AnnouncementProjectTypeCustomRepository {}
