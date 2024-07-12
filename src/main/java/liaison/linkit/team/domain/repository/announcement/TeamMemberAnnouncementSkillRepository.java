package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamMemberAnnouncementSkillRepository extends JpaRepository<TeamMemberAnnouncementSkill, Long> {

    @Query("SELECT teamMemberAnnouncementSkill FROM TeamMemberAnnouncementSkill teamMemberAnnouncementSkill WHERE teamMemberAnnouncementSkill.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    Optional<TeamMemberAnnouncementSkill> findByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Query("DELETE FROM TeamMemberAnnouncementSkill teamMemberAnnouncementSkill WHERE teamMemberAnnouncementSkill.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    void deleteAllByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

}
