package liaison.linkit.team.domain.repository.announcement;

import java.util.List;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberAnnouncementRepository
        extends JpaRepository<TeamMemberAnnouncement, Long>, TeamMemberAnnouncementCustomRepository {

    List<TeamMemberAnnouncement> team(Team team);
}
