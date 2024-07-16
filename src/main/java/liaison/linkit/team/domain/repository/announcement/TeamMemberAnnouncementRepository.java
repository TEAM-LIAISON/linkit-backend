package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
          SELECT DISTINCT tma FROM TeamMemberAnnouncement tma
          JOIN tma.teamProfile tp
            
            
         LEFT JOIN TeamProfileTeamBuildingField tptbf ON tp.id = tptbf.teamProfile.id
         LEFT JOIN TeamBuildingField tbf ON tptbf.teamBuildingField.id = tbf.id
         
         LEFT JOIN ActivityRegion ar ON tp.id = ar.teamProfile.id
         LEFT JOIN Region r ON ar.region.id = r.id
         
         LEFT JOIN TeamMemberAnnouncementJobRole tmajr ON tma.id = tmajr.teamMemberAnnouncement.id
         LEFT JOIN JobRole jr ON tmajr.jobRole.id = jr.id
         
         LEFT JOIN TeamMemberAnnouncementSkill tmas ON tma.id = tmas.teamMemberAnnouncement.id
         LEFT JOIN Skill s ON tmas.skill.id = s.id
         
         LEFT JOIN ActivityMethod am ON tp.id = am.teamProfile.id
         LEFT JOIN ActivityMethodTag amt ON am.activityMethodTag.id = amt.id
         
         WHERE (:teamBuildingFieldNames IS NULL OR tbf.teamBuildingFieldName IN :teamBuildingFieldNames)
         AND (:jobRoleNames IS NULL OR jr.jobRoleName IN :jobRoleNames)
         AND (:skillNames IS NULL OR s.skillName IN :skillNames)
         AND (:cityName IS NULL OR r.cityName = :cityName)
         AND (:divisionName IS NULL OR r.divisionName = :divisionName)
         AND (:activityTagNames IS NULL OR amt.activityTagName IN :activityTagNames)
         """)
    Page<TeamMemberAnnouncement> findAllByOrderByCreatedDateDesc(
            @Param("teamBuildingFieldNames") final List<String> teamBuildingFieldNames,
            @Param("jobRoleNames") final List<String> jobRoleNames,
            @Param("skillNames") final List<String> skillNames,
            @Param("cityName") final String cityName,
            @Param("divisionName") final String divisionName,
            @Param("activityTagNames") final List<String> activityTagNames,
            Pageable pageable
    );

}
