package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface TeamMiniProfileRepository extends JpaRepository<TeamMiniProfile, Long> {

    boolean existsByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);


    Optional<TeamMiniProfile> findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Transactional
    @Modifying
    @Query("DELETE FROM TeamMiniProfile teamMiniProfile WHERE teamMiniProfile.teamProfile.id = :teamProfileId")
    void deleteByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);



    @Query("""
           SELECT tmp FROM TeamMiniProfile tmp
           JOIN tmp.teamProfile tp
           
           LEFT JOIN TeamProfileTeamBuildingField tptbf ON tp.id = tptbf.teamProfile.id
           LEFT JOIN TeamBuildingField tbf ON tptbf.teamBuildingField.id = tbf.id
           
           LEFT JOIN ActivityRegion ar ON tp.id = ar.teamProfile.id
           LEFT JOIN Region r ON ar.teamProfile.id = r.id
            
           LEFT JOIN TeamMemberAnnouncement tma ON tp.id = tma.teamProfile.id
           LEFT JOIN TeamMemberAnnouncementJobRole tmajr ON tma.id = tmajr.teamMemberAnnouncement.id
           LEFT JOIN TeamMemberAnnouncementSkill tmas ON tma.id = tmajr.teamMemberAnnouncement.id
           LEFT JOIN JobRole jr ON tmajr.jobRole.id = jr.id
           LEFT JOIN Skill s ON tmas.skill.id = s.id
           
           LEFT JOIN ActivityMethod am ON tp.id = am.teamProfile.id
           LEFT JOIN ActivityMethodTag amt ON am.teamProfile.id = amt.id
                   
           WHERE (:teamBuildingFieldName IS NULL OR tbf.teamBuildingFieldName = :teamBuildingFieldName)
           AND (:jobRoleName IS NULL OR jr.jobRoleName = :jobRoleName)
           AND (:skillName IS NULL OR s.skillName = :skillName)
           
           AND (:cityName IS NULL OR r.cityName = :cityName)
           AND (:divisionName IS NULL OR r.divisionName = :divisionName)
           AND (:activityTagName IS NULL OR amt.activityTagName = :activityTagName)
           
           ORDER BY tmp.createdDate DESC
           """)
    Page<TeamMiniProfile> findAllByOrderByCreatedDateDesc(
            // 희망 팀빌딩 분야 (V)
            @Param("teamBuildingFieldName") final String teamBuildingFieldName,

            // 보유 직무/역할
            @Param("jobRoleName") final String jobRoleName,
            // 보유 역량
            @Param("skillName") final String skillName,

            // 시/도 (V)
            @Param("cityName") final String cityName,
            // 시/군/구 (V)
            @Param("divisionName") final String divisionName,

            // 활동 방식 태그 이름 (V)
            @Param("activityTagName") final String activityTagName,
            final Pageable pageable
    );
}
