package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberAnnouncementRepository extends JpaRepository<TeamMemberAnnouncement, Long> {
    boolean existsByTeamProfileId(final Long teamProfileId);

    @Query("SELECT teamMemberAnnouncement FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId")
    List<TeamMemberAnnouncement> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
