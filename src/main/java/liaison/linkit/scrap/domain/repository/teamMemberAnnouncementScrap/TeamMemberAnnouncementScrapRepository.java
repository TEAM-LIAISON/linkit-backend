package liaison.linkit.scrap.domain.repository.teamMemberAnnouncementScrap;

import liaison.linkit.scrap.domain.TeamMemberAnnouncementScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberAnnouncementScrapRepository extends JpaRepository<TeamMemberAnnouncementScrap, Long>, TeamMemberAnnouncementScrapCustomRepository {
}
