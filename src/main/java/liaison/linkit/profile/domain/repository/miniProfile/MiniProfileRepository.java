package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long> {
    boolean existsByProfileId(final Long profileId);

    Optional<MiniProfile> findByProfileId(@Param("profileId") final Long profileId);

    @Modifying
    @Transactional // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM MiniProfile miniProfile WHERE miniProfile.profile.id = :profileId")
    void deleteByProfileId(@Param("profileId") final Long profileId);


    @Query("""
           SELECT mp FROM MiniProfile mp
           JOIN mp.profile p
           
           LEFT JOIN ProfileTeamBuildingField ptbf ON p.id = ptbf.profile.id
           LEFT JOIN TeamBuildingField tbf ON ptbf.teamBuildingField.id = tbf.id
           
           LEFT JOIN ProfileJobRole pjr ON p.id = pjr.profile.id
           LEFT JOIN JobRole jr ON pjr.jobRole.id = jr.id
           
           LEFT JOIN ProfileSkill ps ON p.id = ps.profile.id
           LEFT JOIN Skill s ON ps.skill.id = s.id
           
           LEFT JOIN ProfileRegion pr ON p.id = pr.profile.id
           LEFT JOIN Region r ON pr.profile.id = r.id
           
           WHERE (:teamBuildingFieldName IS NULL OR tbf.teamBuildingFieldName = :teamBuildingFieldName)
           AND (:jobRoleName IS NULL OR jr.jobRoleName = :jobRoleName)
           AND (:skillName IS NULL OR s.skillName = :skillName)
           AND (:cityName IS NULL OR r.cityName = :cityName)
           AND (:divisionName IS NULL OR r.divisionName = :divisionName)
           
           ORDER BY mp.createdDate DESC
           """)
    Page<MiniProfile> findAllByOrderByCreatedDateDesc(
            @Param("teamBuildingFieldName") final String teamBuildingFieldName,
            @Param("jobRoleName") final String jobRoleName,
            @Param("skillName") final String skillName,
            @Param("cityName") final String cityName,
            @Param("divisionName") final String divisionName,
            final Pageable pageable
    );
}
