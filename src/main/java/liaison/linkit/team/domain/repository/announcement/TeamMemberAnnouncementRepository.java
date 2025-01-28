package liaison.linkit.team.domain.repository.announcement;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeamMemberAnnouncementRepository
        extends JpaRepository<TeamMemberAnnouncement, Long>, TeamMemberAnnouncementCustomRepository {

    @Modifying
    @Query("DELETE FROM TeamMemberAnnouncement t WHERE t.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    List<TeamMemberAnnouncement> team(Team team);
}
