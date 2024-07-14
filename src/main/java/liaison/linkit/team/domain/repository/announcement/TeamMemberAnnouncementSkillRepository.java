package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMemberAnnouncementSkillRepository extends JpaRepository<TeamMemberAnnouncementSkill, Long> {

    @Query("SELECT teamMemberAnnouncementSkill FROM TeamMemberAnnouncementSkill teamMemberAnnouncementSkill WHERE teamMemberAnnouncementSkill.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    List<TeamMemberAnnouncementSkill> findAllByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamMemberAnnouncementSkill teamMemberAnnouncementSkill WHERE teamMemberAnnouncementSkill.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    void deleteAllByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

}
