package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMemberAnnouncementJobRoleRepository extends JpaRepository<TeamMemberAnnouncementJobRole, Long> {

    @Query("SELECT teamMemberAnnouncementJobRole FROM TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole WHERE teamMemberAnnouncementJobRole.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    List<TeamMemberAnnouncementJobRole> findAllByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole WHERE teamMemberAnnouncementJobRole.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    void deleteAllByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

}
