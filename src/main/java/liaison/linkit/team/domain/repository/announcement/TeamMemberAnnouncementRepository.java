package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberAnnouncementRepository
        extends JpaRepository<TeamMemberAnnouncement, Long>,
                TeamMemberAnnouncementCustomRepository {}
