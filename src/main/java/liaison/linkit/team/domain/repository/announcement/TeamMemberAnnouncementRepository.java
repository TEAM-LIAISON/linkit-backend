package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMemberAnnouncementRepository extends JpaRepository<TeamMemberAnnouncement, Long> {
    boolean existsByTeamProfileId(final Long teamProfileId);

    @Query("SELECT teamMemberAnnouncement FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId")
    List<TeamMemberAnnouncement> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Modifying
    @Transactional
    // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
